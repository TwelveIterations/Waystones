package net.blay09.mods.waystones.core;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.shogi.context.executor.DeferredEffectExecutor;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.event.WaystoneTeleportEvent;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.network.message.ClientboundTeleportEffectPacket;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class WaystoneTeleportManager {

    public static Collection<? extends Entity> findPets(LivingEntity entity) {
        return entity.level().getEntitiesOfClass(TamableAnimal.class, new AABB(entity.blockPosition()).inflate(10),
                pet -> Optional.ofNullable(pet.getOwnerReference())
                        .map(it -> it.matches(entity))
                        .orElse(false) && !pet.isOrderedToSit() && !pet.isLeashed() && !WaystonePermissionManager.isEntityDeniedTeleports(pet)
        );
    }

    public static List<Mob> findLeashedAnimals(@Nullable Entity entity) {
        if (entity == null) {
            return Collections.emptyList();
        }
        return entity.level().getEntitiesOfClass(Mob.class, new AABB(entity.blockPosition()).inflate(10),
                e -> entity.equals(e.getLeashHolder())
        );
    }

    public static Either<List<Entity>, WaystoneTeleportError> doTeleport(WaystoneTeleportContext context) {
        final var server = context.getEntity().level().getServer();
        if (server == null) {
            return Either.right(new WaystoneTeleportError.NotOnServer());
        }

        return resolveDestination(server, context.getTargetWaystone()).flatMap(it -> doTeleport(context, it));
    }

    public static CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> forceTeleportAsync(WaystoneTeleportContext context) {
        try {
            return prepareTeleport(context, true)
                    .thenApply(result -> result.flatMap(destination -> doTeleport(context, destination)))
                    .exceptionally(WaystoneTeleportManager::handleUnexpectedAsyncFailure);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(unexpectedFailure(e));
        }
    }

    private static CompletableFuture<Either<TeleportDestination, WaystoneTeleportError>> prepareTeleport(WaystoneTeleportContext context, boolean forced) {
        final var entity = context.getEntity();
        final var sourceLevel = entity.level();
        final var server = sourceLevel.getServer();
        if (server == null) {
            return CompletableFuture.completedFuture(Either.right(new WaystoneTeleportError.NotOnServer()));
        }

        return loadDestinationChunksAsync(server, context.getTargetWaystone())
                .thenApply(loadResult -> loadResult.flatMap(_ -> forced ? Either.left(null) : validatePendingTeleport(context, sourceLevel, server)))
                .thenApply(validationResult -> validationResult.flatMap(_ -> resolveDestination(server, context.getTargetWaystone())));
    }

    private static CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>> loadDestinationChunksAsync(MinecraftServer server, Waystone targetWaystone) {
        final var targetLevel = server.getLevel(targetWaystone.getDimension());
        if (targetLevel == null) {
            return CompletableFuture.completedFuture(Either.right(new WaystoneTeleportError.InvalidDimension(targetWaystone.getDimension())));
        }

        return loadDestinationChunks(server, targetLevel, targetWaystone);
    }

    private static Either<@Nullable Void, WaystoneTeleportError> validatePendingTeleport(WaystoneTeleportContext context, Level sourceLevel, MinecraftServer server) {
        if (!arePendingEntitiesStillValid(context, sourceLevel)) {
            Waystones.logger.debug("Discarding pending waystone teleport because one or more transported entities are no longer valid.");
            return Either.right(new WaystoneTeleportError.TeleportNoLongerValid());
        }

        if (!isSourceWaystoneStillInRange(server, context)) {
            Waystones.logger.debug("Discarding pending waystone teleport because the player moved away from the source waystone or the source was removed.");
            return Either.right(new WaystoneTeleportError.SourceWaystoneOutOfRange());
        }

        if (!isWarpItemStillPresent(context)) {
            Waystones.logger.debug("Discarding pending waystone teleport because the source item is no longer present.");
            return Either.right(new WaystoneTeleportError.SourceItemMissing());
        }

        final var targetWaystone = context.getTargetWaystone();
        final var targetLevel = server.getLevel(targetWaystone.getDimension());
        if (targetLevel == null) {
            return Either.right(new WaystoneTeleportError.InvalidDimension(targetWaystone.getDimension()));
        }

        if (!targetWaystone.isValid()) {
            Waystones.logger.debug("Discarding pending waystone teleport because target waystone {} is no longer valid.", targetWaystone.getWaystoneUid());
            return Either.right(new WaystoneTeleportError.InvalidWaystone(targetWaystone));
        }

        if (!targetWaystone.isValidInLevel(targetLevel)) {
            Waystones.logger.debug("Discarding pending waystone teleport because target waystone {} is missing in {}.", targetWaystone.getWaystoneUid(), targetLevel.dimension());
            return Either.right(new WaystoneTeleportError.MissingWaystone(targetWaystone));
        }

        return Either.left(null);
    }

    public static Either<List<Entity>, WaystoneTeleportError> doTeleport(WaystoneTeleportContext context, TeleportDestination destination) {
        final var sourceLevel = (ServerLevel) context.getEntity().level();
        List<Entity> teleportedEntities = teleportEntityAndAttached(context.getEntity(), context, destination);
        context.getAdditionalEntities()
                .forEach(additionalEntity -> teleportedEntities.addAll(teleportEntityAndAttached(additionalEntity, context, destination)));

        final var sourcePos = context.getEntity().blockPosition();
        final var targetLevel = (ServerLevel) destination.level();
        final var targetPos = BlockPos.containing(destination.location());

        BlockEntity targetTileEntity = targetLevel.getBlockEntity(targetPos);
        if (targetTileEntity instanceof WarpPlateBlockEntity warpPlate) {
            teleportedEntities.forEach(warpPlate::markEntityForCooldown);
        }

        if (context.playsSound()) {
            sourceLevel.playSound(context.getEntity(), sourcePos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.01f, 1f);
            targetLevel.playSound(null, targetPos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.05f, 1f);
        }

        if (context.playsEffect()) {
            teleportedEntities.forEach(additionalEntity -> Balm.networking().sendToTracking(sourceLevel, sourcePos, new ClientboundTeleportEffectPacket(sourcePos)));
            Balm.networking().sendToTracking(targetLevel, targetPos, new ClientboundTeleportEffectPacket(targetPos));
        }

        teleportedEntities.forEach(entity -> {
            final var nestedContext = context.fork().withEntity(entity).withBlockEntity(targetTileEntity);
            WaystonesRules.afterWarpEffects.getOrDefault(nestedContext);
        });

        return Either.left(teleportedEntities);
    }

    private static List<Entity> teleportEntityAndAttached(Entity entity, WaystoneTeleportContext context, TeleportDestination destination) {
        final var teleportedEntities = new ArrayList<Entity>();

        final var targetLevel = (ServerLevel) destination.level();
        final var targetLocation = destination.location();
        final var targetDirection = destination.direction();

        final var mount = entity.getVehicle();
        Entity teleportedMount = null;
        if (mount != null) {
            teleportedMount = teleportEntity(mount, targetLevel, targetLocation, targetDirection);
            teleportedEntities.add(teleportedMount);
        }

        final List<Mob> leashedEntities = context.getLeashedEntities();
        final List<Entity> teleportedLeashedEntities = new ArrayList<>();
        leashedEntities.forEach(leashedEntity -> {
            Entity teleportedLeashedEntity = teleportEntity(leashedEntity, targetLevel, targetLocation, targetDirection);
            teleportedEntities.add(teleportedLeashedEntity);
            teleportedLeashedEntities.add(teleportedLeashedEntity);
        });

        final var teleportedEntity = teleportEntity(entity, targetLevel, targetLocation, targetDirection);
        teleportedEntities.add(teleportedEntity);

        // We have to update the leashedToEntity in case the player was cloned during dimensional teleport
        teleportedLeashedEntities.forEach(teleportedLeashedEntity -> {
            if (teleportedLeashedEntity instanceof Mob teleportedLeashedMob) {
                teleportedLeashedMob.setLeashedTo(teleportedEntity, true);
            }
        });

        if (teleportedMount != null) {
            // TODO We do not remount currently. It causes weird sync issues and it seems that Vanilla does not do it either.
            //      Would have to look further at what point it's safe to remount without triggering movement correction.
        }

        return teleportedEntities;
    }

    private static Entity teleportEntity(Entity entity, ServerLevel targetWorld, Vec3 targetPos3d, Direction direction) {
        float yaw = direction.toYRot();
        double x = targetPos3d.x;
        double y = targetPos3d.y;
        double z = targetPos3d.z;
        entity.resetFallDistance();
        if (entity instanceof ServerPlayer) {
            entity.stopRiding();
            if (((ServerPlayer) entity).isSleeping()) {
                ((ServerPlayer) entity).stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level()) {
                ((ServerPlayer) entity).connection.teleport(x, y, z, yaw, entity.getXRot());
            } else {
                entity.teleportTo(targetWorld, x, y, z, Set.of(), yaw, entity.getXRot(), false);
            }

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level()) {
                entity.snapTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
            } else {
                entity.unRide();
                Entity oldEntity = entity;
                entity = entity.getType().create(targetWorld, EntitySpawnReason.DIMENSION_TRAVEL);
                if (entity == null) {
                    return oldEntity;
                }

                entity.restoreFrom(oldEntity);
                entity.snapTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
                oldEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetWorld.addDuringTeleport(entity);
            }
        }

        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
            entity.setOnGround(true);
        }

        if (entity instanceof PathfinderMob) {
            ((PathfinderMob) entity).getNavigation().stop();
        }

        sendHackySyncPacketsAfterTeleport(entity);

        return entity;
    }

    private static Either<TeleportDestination, WaystoneTeleportError> resolveDestination(MinecraftServer server, Waystone waystone) {
        final var level = server.getLevel(waystone.getDimension());
        if (level == null) {
            return Either.right(new WaystoneTeleportError.InvalidDimension(waystone.getDimension()));
        }

        return waystone.resolveDestination(level)
                .map(Either::<TeleportDestination, WaystoneTeleportError>left)
                .orElseGet(() -> Either.right(new WaystoneTeleportError.InvalidWaystone(waystone)));
    }

    private static void sendHackySyncPacketsAfterTeleport(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            // No idea why this is still needed since we're using the same code as /tp. Maybe /tp is broken too for interdimensional travel.
            player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        }
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(WaystoneTeleportContext context) {
        final var validationResult = validateTeleportStart(context);
        if (validationResult.right().isPresent()) {
            return Either.right(validationResult.right().orElseThrow());
        }

        if (context.executor() instanceof DeferredEffectExecutor executor) {
            executor.execute();
        }

        return doTeleport(context)
                .ifLeft(teleportedEntities -> WaystoneTeleportEvent.After.EVENT.invoker().accept(new WaystoneTeleportEvent.After(context, teleportedEntities)));
    }

    public static CompletableFuture<Either<List<Entity>, WaystoneTeleportError>> tryTeleportAsync(WaystoneTeleportContext context) {
        try {
            final var validationResult = validateTeleportStart(context);
            if (validationResult.right().isPresent()) {
                return CompletableFuture.completedFuture(Either.right(validationResult.right().orElseThrow()));
            }

            return prepareTeleport(context, false)
                    .thenApply(result -> result.flatMap(destination -> {
                        if (context instanceof WaystoneTeleportContextImpl contextImpl) {
                            contextImpl.invalidateRequirements();
                        }

                        if (context.getRequirements().right().isPresent()) {
                            return Either.right(new WaystoneTeleportError.RequirementsNotMet());
                        }

                        if (context.executor() instanceof DeferredEffectExecutor executor) {
                            executor.execute();
                        }

                        return doTeleport(context, destination)
                                .ifLeft(teleportedEntities -> WaystoneTeleportEvent.After.EVENT.invoker().accept(new WaystoneTeleportEvent.After(context, teleportedEntities)));
                    }))
                    .exceptionally(WaystoneTeleportManager::handleUnexpectedAsyncFailure);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(unexpectedFailure(e));
        }
    }

    private static Either<List<Entity>, WaystoneTeleportError> handleUnexpectedAsyncFailure(Throwable throwable) {
        final var cause = throwable instanceof CompletionException && throwable.getCause() != null ? throwable.getCause() : throwable;
        return unexpectedFailure(cause);
    }

    private static Either<List<Entity>, WaystoneTeleportError> unexpectedFailure(Throwable throwable) {
        Waystones.logger.error("Unexpected error while processing async waystone teleport.", throwable);
        return Either.right(new WaystoneTeleportError.TeleportFailed());
    }

    private static CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>> loadDestinationChunks(MinecraftServer server, ServerLevel targetLevel, Waystone targetWaystone) {
        final var chunkPositions = getDestinationChunkPositions(targetWaystone);
        final var result = new CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>>();
        final var remaining = new int[]{chunkPositions.size()};

        for (final var chunkPos : chunkPositions) {
            targetLevel.getChunkSource().getChunkFuture(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, true)
                    .whenComplete((chunkResult, throwable) -> server.execute(() -> {
                            if (result.isDone()) {
                                return;
                            }

                            if (throwable != null) {
                                Waystones.logger.warn("Failed to load destination chunk {} for waystone teleport in {}.", chunkPos, targetWaystone.getDimension(), throwable);
                                result.complete(Either.right(new WaystoneTeleportError.DestinationChunkLoadFailed(targetWaystone.getDimension(), chunkPos, throwable.getMessage())));
                            } else if (!chunkResult.isSuccess()) {
                                Waystones.logger.warn("Failed to load destination chunk {} for waystone teleport in {}: {}", chunkPos, targetWaystone.getDimension(), chunkResult.getError());
                                result.complete(Either.right(new WaystoneTeleportError.DestinationChunkLoadFailed(targetWaystone.getDimension(), chunkPos, chunkResult.getError())));
                            } else if (--remaining[0] == 0) {
                                result.complete(Either.left(null));
                            }
                        }));
        }

        return result;
    }

    private static Set<ChunkPos> getDestinationChunkPositions(Waystone targetWaystone) {
        final var targetPos = targetWaystone.getPos();
        final var chunkPositions = new LinkedHashSet<ChunkPos>();
        chunkPositions.add(ChunkPos.containing(targetPos));
        for (final var direction : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH)) {
            chunkPositions.add(ChunkPos.containing(targetPos.relative(direction)));
        }
        return chunkPositions;
    }

    private static Either<@Nullable Void, WaystoneTeleportError> validateTeleportStart(WaystoneTeleportContext context) {
        WaystoneTeleportEvent.Before event = new WaystoneTeleportEvent.Before(context);
        WaystoneTeleportEvent.Before.EVENT.invoker().accept(event);
        if (event.isCanceled()) {
            return Either.right(new WaystoneTeleportError.CancelledByEvent());
        }

        final var entity = context.getEntity();
        if (!isPendingEntityStillValid(entity, entity.level())) {
            return Either.right(new WaystoneTeleportError.TeleportNoLongerValid());
        }

        if (!isSourceWaystoneInRange(context)) {
            return Either.right(new WaystoneTeleportError.SourceWaystoneOutOfRange());
        }

        if (!isWarpItemStillPresent(context)) {
            return Either.right(new WaystoneTeleportError.SourceItemMissing());
        }

        if (!context.getLeashedEntities().isEmpty()) {
            if (WaystonesConfig.getActive().rules.transportLeashed == WaystonesConfig.TransportMobs.DISABLED) {
                return Either.right(new WaystoneTeleportError.LeashedWarpDenied());
            }

            for (final var leashedEntity : context.getLeashedEntities()) {
                if (WaystonePermissionManager.isEntityDeniedTeleports(leashedEntity)) {
                    return Either.right(new WaystoneTeleportError.SpecificLeashedWarpDenied(leashedEntity));
                }
            }

            if (context.isDimensionalTeleport() && WaystonesConfig.getActive().rules.transportLeashed == WaystonesConfig.TransportMobs.SAME_DIMENSION) {
                return Either.right(new WaystoneTeleportError.LeashedDimensionalWarpDenied());
            }
        }

        if (context.getRequirements().right().isPresent()) {
            return Either.right(new WaystoneTeleportError.RequirementsNotMet());
        }

        return Either.left(null);
    }

    private static boolean isPendingEntityStillValid(Entity entity, Level expectedLevel) {
        return entity.isAlive()
                && !entity.isRemoved()
                && entity.level() == expectedLevel
                && (!(entity instanceof ServerPlayer player) || !player.hasDisconnected());
    }

    private static boolean arePendingEntitiesStillValid(WaystoneTeleportContext context, Level expectedLevel) {
        if (!isPendingEntityStillValid(context.getEntity(), expectedLevel)) {
            return false;
        }

        for (final var additionalEntity : context.getAdditionalEntities()) {
            if (!isPendingEntityStillValid(additionalEntity, expectedLevel)) {
                return false;
            }
        }

        for (final var leashedEntity : context.getLeashedEntities()) {
            if (!isPendingEntityStillValid(leashedEntity, expectedLevel)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSourceWaystoneInRange(WaystoneTeleportContext context) {
        return context.getFromWaystone()
                .map(fromWaystone -> context.getEntity().level().dimension() == fromWaystone.getDimension()
                        && context.getEntity().distanceToSqr(Vec3.atCenterOf(fromWaystone.getPos())) <= 64)
                .orElse(true);
    }

    private static boolean isSourceWaystoneStillInRange(MinecraftServer server, WaystoneTeleportContext context) {
        return context.getFromWaystone()
                .map(fromWaystone -> {
                    final var sourceLevel = server.getLevel(fromWaystone.getDimension());
                    return sourceLevel != null
                            && SavedDataWaystonesStore.get(server).getWaystoneById(fromWaystone.getWaystoneUid()).isPresent()
                            && fromWaystone.isValidInLevel(sourceLevel)
                            && context.getEntity().level().dimension() == fromWaystone.getDimension()
                            && context.getEntity().distanceToSqr(Vec3.atCenterOf(fromWaystone.getPos())) <= 64;
                })
                .orElse(true);
    }

    private static boolean isWarpItemStillPresent(WaystoneTeleportContext context) {
        final var warpHand = context.getWarpHand();
        if (warpHand == null) {
            return true;
        }

        if (!(context.getEntity() instanceof ServerPlayer player)) {
            return true;
        }

        final var itemInHand = player.getItemInHand(warpHand);
        return !itemInHand.isEmpty() && ItemStack.isSameItemSameComponents(itemInHand, context.getWarpItem());
    }

    public static Collection<Entity> findPassengers(@Nullable Entity entity) {
        if (entity == null) {
            return Collections.emptyList();
        }
        final var passengers = entity.getPassengers();
        final var result = new ArrayList<>(passengers);
        final var vehicle = entity.getVehicle();
        if (vehicle != null) {
            result.addAll(vehicle.getPassengers());
        }
        result.remove(entity);
        return result;
    }
}
