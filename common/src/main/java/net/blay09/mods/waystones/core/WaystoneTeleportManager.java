package net.blay09.mods.waystones.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.event.WaystoneTeleportEntityEvent;
import net.blay09.mods.waystones.api.event.WaystoneTeleportEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WaystoneTeleportManager {

    public static Collection<? extends Entity> findPets(Entity entity) {
        return entity.level().getEntitiesOfClass(TamableAnimal.class, new AABB(entity.blockPosition()).inflate(10),
                pet -> entity.getUUID()
                        .equals(pet.getOwnerUUID()) && !pet.isOrderedToSit() && !pet.isLeashed() && !WaystonePermissionManager.isEntityDeniedTeleports(pet)
        );
    }

    public static List<Mob> findLeashedAnimals(Entity player) {
        return player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    public static WaystoneTeleportResult teleport(WaystoneTeleportContext context) {
        final var server = context.getEntity().getServer();
        if (server == null) {
            throw new IllegalStateException("must only be called with a server-side entity");
        }

        final var result = resolveDestination(server, context.getTargetWaystone())
                .map(destination -> performTeleport(context, destination), WaystoneTeleportResult::failed);
        Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Complete(context,
                result.primaryResult().orElse(null),
                result.additionalResults(),
                result.teleportError().orElse(null)));
        return result;
    }

    public static CompletableFuture<WaystoneTeleportResult> forceTeleportAsync(WaystoneTeleportContext context) {
        return prepareTeleport(context, true)
                .thenApply(result -> result.map(destination -> performTeleport(context, destination), WaystoneTeleportResult::failed))
                .thenApply(result -> {
                    Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Complete(context,
                            result.primaryResult().orElse(null),
                            result.additionalResults(),
                            result.teleportError().orElse(null)));
                    return result;
                })
                .whenComplete((result, throwable) -> crashOnUnexpectedAsyncFailure(context, throwable));
    }

    private static CompletableFuture<Either<TeleportDestination, WaystoneTeleportError>> prepareTeleport(WaystoneTeleportContext context, boolean forced) {
        final var entity = context.getEntity();
        final var sourceLevel = entity.level();
        final var server = sourceLevel.getServer();
        if (server == null) {
            throw new IllegalStateException("must only be called with a server-side entity");
        }

        return loadDestinationChunksAsync(server, context)
                .thenApply(loadResult -> loadResult.flatMap(ignored -> forced ? success() : validatePendingTeleport(context, sourceLevel, server)))
                .thenApply(validationResult -> validationResult.flatMap(ignored -> resolveDestination(server, context.getTargetWaystone())));
    }

    private static CompletableFuture<Either<Void, WaystoneTeleportError>> loadDestinationChunksAsync(MinecraftServer server, WaystoneTeleportContext context) {
        final var targetWaystone = context.getTargetWaystone();
        final var targetLevel = server.getLevel(targetWaystone.getDimension());
        if (targetLevel == null) {
            return CompletableFuture.completedFuture(Either.right(new WaystoneTeleportError.InvalidDimension(targetWaystone.getDimension())));
        }

        return loadDestinationChunks(server, targetLevel, context);
    }

    private static Either<Void, WaystoneTeleportError> validatePendingTeleport(WaystoneTeleportContext context, Level sourceLevel, MinecraftServer server) {
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

        return success();
    }

    private static WaystoneTeleportResult performTeleport(WaystoneTeleportContext context, TeleportDestination destination) {
        final var result = new WaystoneTeleportResult(new ArrayList<>());
        final var sourceLevel = (ServerLevel) context.getEntity().level();
        final var batch = new EntityTeleportBatch(context, destination, result);
        batch.teleportEntityAndAttached(context.getEntity());
        context.getAdditionalEntities().forEach(batch::teleportEntityAndAttached);
        batch.restoreMounts();

        final var teleportedEntities = result.teleportedEntities();
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
            teleportedEntities.forEach(additionalEntity -> Balm.networking().sendToTracking(sourceLevel, sourcePos, new TeleportEffectMessage(sourcePos)));
            Balm.networking().sendToTracking(targetLevel, targetPos, new TeleportEffectMessage(targetPos));
        }

        if (context.appliesModifiers() && targetTileEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            teleportedEntities.forEach(waystoneBlockEntity::applyModifierEffects);
        }

        return result;
    }

    public static EntityTeleportResult teleportEntity(WaystoneTeleportContext context, Entity entity, TeleportDestination destination) {
        ServerLevel targetLevel = (ServerLevel) destination.level();
        Vec3 targetPosition = destination.location();
        Direction direction = destination.direction();
        final var event = new WaystoneTeleportEntityEvent.Pre(context, entity, destination, targetLevel, targetPosition, direction);
        Balm.getEvents().fireEvent(event);
        final var result = event.getOverrideResult();
        if (result.isPresent()) {
            Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, entity, result.get(), destination, event.getTargetLevel(), event.getTargetPosition(), event.getDirection()));
            return result.get();
        }

        if (event.isCanceled()) {
            final var cancelResult = EntityTeleportResult.failed(entity, destination, new WaystoneTeleportError.CancelledByEvent());
            Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, entity, cancelResult, destination, event.getTargetLevel(), event.getTargetPosition(), event.getDirection()));
            return cancelResult;
        }

        final var originalEntity = entity;
        targetLevel = event.getTargetLevel();
        targetPosition = event.getTargetPosition();
        direction = event.getDirection();

        float yaw = Mth.wrapDegrees(direction.toYRot());
        float pitch = Mth.wrapDegrees(entity.getXRot());
        double x = targetPosition.x;
        double y = targetPosition.y;
        double z = targetPosition.z;
        if (!Level.isInSpawnableBounds(BlockPos.containing(x, y, z))) {
            final var failedResult = EntityTeleportResult.failed(entity, destination, new WaystoneTeleportError.DestinationOutOfBounds());
            Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, originalEntity, failedResult, destination, targetLevel, targetPosition, direction));
            return failedResult;
        }

        if (!entity.teleportTo(targetLevel, x, y, z, Collections.emptySet(), yaw, pitch)) {
            final var failedResult = EntityTeleportResult.failed(entity, destination, new WaystoneTeleportError.TeleportFailed());
            Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, originalEntity, failedResult, destination, targetLevel, targetPosition, direction));
            return failedResult;
        }

        if (entity.isRemoved()) {
            final var teleportedEntity = targetLevel.getEntity(entity.getUUID());
            if (teleportedEntity == null) {
                final var failedResult = EntityTeleportResult.failed(entity, destination, new WaystoneTeleportError.TeleportFailed());
                Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, originalEntity, failedResult, destination, targetLevel, targetPosition, direction));
                return failedResult;
            }

            entity = teleportedEntity;
        }

        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
            entity.setOnGround(true);
        }

        if (entity instanceof PathfinderMob) {
            ((PathfinderMob) entity).getNavigation().stop();
        }

        ((WaystoneTeleportedEntity) entity).waystones$markTeleportedByWaystone();
        sendHackySyncPacketsAfterTeleport(entity);

        final var teleportResult = EntityTeleportResult.success(entity, destination, new TeleportDestination(targetLevel, targetPosition, direction));
        Balm.getEvents().fireEvent(new WaystoneTeleportEntityEvent.Post(context, originalEntity, teleportResult, destination, targetLevel, targetPosition, direction));
        return teleportResult;
    }

    private static boolean shouldTeleportOffsetByFacing(Waystone waystone) {
        return !(waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE));
    }

    private static boolean hasSpaceToTeleport(Level level, BlockPos pos) {
        final var posAbove = pos.above();
        return !level.getBlockState(pos).isSuffocating(level, pos) && !level.getBlockState(posAbove).isSuffocating(level, posAbove);
    }

    private static boolean isValidTeleportOffset(Level level, BlockPos pos, Direction direction, BlockPos neighborPos) {
        return true;
    }

    private static Optional<Direction> findTeleportOffsetDirection(Level level, BlockPos pos, Waystone waystone, Direction preferred) {
        if (!shouldTeleportOffsetByFacing(waystone)) {
            return Optional.empty();
        }

        // Use a list to keep order intact - it might check one direction twice, but no one cares
        final var directionCandidates = Lists.newArrayList(preferred, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (final var candidate : directionCandidates) {
            final var offsetPos = pos.relative(candidate);
            if (hasSpaceToTeleport(level, offsetPos) && isValidTeleportOffset(level, pos, candidate, offsetPos)) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    public static Optional<TeleportDestination> resolveDefaultDestination(ServerLevel level, Waystone waystone) {
        final var pos = waystone.getPos();
        final var state = level.getBlockState(pos);
        final var direction = state.hasProperty(WaystoneBlock.FACING) ? state.getValue(WaystoneBlock.FACING) : Direction.NORTH;
        final var offsetDirection = findTeleportOffsetDirection(level, pos, waystone, direction);

        final var targetPos = offsetDirection.map(pos::relative).orElse(pos);
        final var location = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        return Optional.of(new TeleportDestination(level, location, offsetDirection.orElse(direction)));
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

    public static WaystoneTeleportResult tryTeleport(WaystoneTeleportContext context) {
        final WaystoneTeleportResult result;
        final var validationResult = validateTeleportStart(context);
        if (validationResult.right().isPresent()) {
            result = WaystoneTeleportResult.failed(validationResult.right().orElseThrow());
        } else {
            consumeRequirements(context);

            final var server = context.getEntity().getServer();
            if (server == null) {
                throw new IllegalStateException("Cannot perform a waystone teleport without a server");
            } else {
                result = resolveDestination(server, context.getTargetWaystone())
                        .map(destination -> {
                            final var teleportResult = performTeleport(context, destination);
                            if (teleportResult.isSuccessful()) {
                                Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Post(context, teleportResult.teleportedEntities()));
                            }
                            return teleportResult;
                        }, WaystoneTeleportResult::failed);
            }
        }

        Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Complete(context,
                result.primaryResult().orElse(null),
                result.additionalResults(),
                result.teleportError().orElse(null)));
        return result;
    }

    public static CompletableFuture<WaystoneTeleportResult> tryTeleportAsync(WaystoneTeleportContext context) {
        final var validationResult = validateTeleportStart(context);
        if (validationResult.right().isPresent()) {
            final var result = WaystoneTeleportResult.failed(validationResult.right().orElseThrow());
            Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Complete(context,
                    result.primaryResult().orElse(null),
                    result.additionalResults(),
                    result.teleportError().orElse(null)));
            return CompletableFuture.completedFuture(result);
        }

        return prepareTeleport(context, false)
                .thenApply(result -> result.map(destination -> {
                    final var delayedRequirementResult = validateRequirements(context);
                    if (delayedRequirementResult.right().isPresent()) {
                        return WaystoneTeleportResult.failed(delayedRequirementResult.right().orElseThrow());
                    }

                    consumeRequirements(context);

                    final var teleportResult = performTeleport(context, destination);
                    if (teleportResult.isSuccessful()) {
                        Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Post(context, teleportResult.teleportedEntities()));
                    }
                    return teleportResult;
                }, WaystoneTeleportResult::failed))
                .thenApply(result -> {
                    Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Complete(context,
                            result.primaryResult().orElse(null),
                            result.additionalResults(),
                            result.teleportError().orElse(null)));
                    return result;
                })
                .whenComplete((result, throwable) -> crashOnUnexpectedAsyncFailure(context, throwable));
    }

    private static void crashOnUnexpectedAsyncFailure(WaystoneTeleportContext context, Throwable throwable) {
        if (throwable == null) {
            return;
        }

        final var server = context.getEntity().getServer();
        if (server != null) {
            server.execute(() -> {
                throw new RuntimeException("Unexpected error while processing waystone teleport.", throwable);
            });
        }
    }

    private static CompletableFuture<Either<Void, WaystoneTeleportError>> loadDestinationChunks(MinecraftServer server, ServerLevel targetLevel, WaystoneTeleportContext context) {
        final var targetWaystone = context.getTargetWaystone();
        final var chunkPositions = getDestinationChunkPositions(targetWaystone);
        final var chunkLoadFuture = new CompletableFuture<Either<Void, WaystoneTeleportError>>();
        final var event = new WaystoneTeleportEvent.Prepare(context, chunkPositions);
        Balm.getEvents().fireEvent(event);
        var effectiveFuture = chunkLoadFuture;
        for (final var preparationTask : event.getPreparationTasks()) {
            effectiveFuture = effectiveFuture.thenCompose(preparationTask);
        }

        final var chunkPositionsToLoad = new LinkedHashSet<>(event.getChunkPositions());
        if (chunkPositionsToLoad.isEmpty()) {
            chunkLoadFuture.complete(success());
            return effectiveFuture;
        }

        final var remaining = new int[]{chunkPositionsToLoad.size()};

        for (final var chunkPos : chunkPositionsToLoad) {
            targetLevel.getChunkSource().getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true)
                    .whenComplete((chunkResult, throwable) -> server.execute(() -> {
                        if (chunkLoadFuture.isDone()) {
                            return;
                        }

                        if (throwable != null) {
                            Waystones.logger.warn("Failed to load destination chunk {} for waystone teleport in {}.", chunkPos, targetWaystone.getDimension(), throwable);
                            chunkLoadFuture.complete(Either.right(new WaystoneTeleportError.DestinationChunkLoadFailed(targetWaystone.getDimension(), chunkPos, throwable.getMessage())));
                        } else if (!chunkResult.isSuccess()) {
                            final var reason = chunkResult.getError();
                            Waystones.logger.warn("Failed to load destination chunk {} for waystone teleport in {}: {}", chunkPos, targetWaystone.getDimension(), reason);
                            chunkLoadFuture.complete(Either.right(new WaystoneTeleportError.DestinationChunkLoadFailed(targetWaystone.getDimension(), chunkPos, reason)));
                        } else if (--remaining[0] == 0) {
                            chunkLoadFuture.complete(success());
                        }
                    }));
        }

        return effectiveFuture;
    }

    private static Set<ChunkPos> getDestinationChunkPositions(Waystone targetWaystone) {
        final var targetPos = targetWaystone.getPos();
        final var chunkPositions = new LinkedHashSet<ChunkPos>();
        chunkPositions.add(new ChunkPos(targetPos));
        for (final var direction : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH)) {
            chunkPositions.add(new ChunkPos(targetPos.relative(direction)));
        }
        return chunkPositions;
    }

    private static Either<Void, WaystoneTeleportError> validateTeleportStart(WaystoneTeleportContext context) {
        WaystoneTeleportEvent.Pre event = new WaystoneTeleportEvent.Pre(context);
        Balm.getEvents().fireEvent(event);
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
            if (WaystonesConfig.getActive().teleports.transportLeashed == WaystonesConfigData.TransportMobs.DISABLED) {
                return Either.right(new WaystoneTeleportError.LeashedWarpDenied());
            }

            for (final var leashedEntity : context.getLeashedEntities()) {
                if (WaystonePermissionManager.isEntityDeniedTeleports(leashedEntity)) {
                    return Either.right(new WaystoneTeleportError.SpecificLeashedWarpDenied(leashedEntity));
                }
            }

            if (context.isDimensionalTeleport() && WaystonesConfig.getActive().teleports.transportLeashed == WaystonesConfigData.TransportMobs.SAME_DIMENSION) {
                return Either.right(new WaystoneTeleportError.LeashedDimensionalWarpDenied());
            }
        }

        return validateRequirements(context);
    }

    private static Either<Void, WaystoneTeleportError> validateRequirements(WaystoneTeleportContext context) {
        if (context.getEntity() instanceof Player player && !context.getRequirements().canAfford(player) && !player.getAbilities().instabuild) {
            return Either.right(new WaystoneTeleportError.NotEnoughXp());
        }

        return success();
    }

    private static void consumeRequirements(WaystoneTeleportContext context) {
        if (context.getEntity() instanceof Player player) {
            context.getRequirements().consume(player);
        }
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
                            && WaystoneManagerImpl.get(server).getWaystoneById(fromWaystone.getWaystoneUid()).isPresent()
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

    private static Either<Void, WaystoneTeleportError> success() {
        return Either.left(null);
    }

    public static Collection<Entity> findPassengers(Entity entity) {
        final var passengers = entity.getPassengers();
        final var result = new ArrayList<>(passengers);
        final var vehicle = entity.getControlledVehicle();
        if (vehicle != null) {
            result.addAll(vehicle.getPassengers());
        }
        result.remove(entity);
        return result;
    }
}
