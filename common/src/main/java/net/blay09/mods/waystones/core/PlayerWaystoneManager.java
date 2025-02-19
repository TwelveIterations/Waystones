package net.blay09.mods.waystones.core;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.config.DimensionalWarp;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.network.message.TeleportEffectMessage;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PlayerWaystoneManager {

    private static final Logger logger = LogManager.getLogger();

    private static final IPlayerWaystoneData persistentPlayerWaystoneData = new PersistentPlayerWaystoneData();
    private static final IPlayerWaystoneData inMemoryPlayerWaystoneData = new InMemoryPlayerWaystoneData();

    public static boolean mayBreakWaystone(Player player, BlockGetter world, BlockPos pos) {
        if (WaystonesConfig.getActive().restrictions.restrictToCreative && !player.getAbilities().instabuild) {
            return false;
        }

        return WaystoneManager.get(player.getServer()).getWaystoneAt(world, pos).map(waystone -> {

            if (!player.getAbilities().instabuild) {
                if (waystone.wasGenerated() && WaystonesConfig.getActive().restrictions.generatedWaystonesUnbreakable) {
                    return false;
                }

                boolean isGlobal = waystone.isGlobal();
                boolean mayBreakGlobalWaystones = !WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode;
                return !isGlobal || mayBreakGlobalWaystones;
            }


            return true;
        }).orElse(true);

    }

    public static boolean mayPlaceWaystone(@Nullable Player player) {
        return !WaystonesConfig.getActive().restrictions.restrictToCreative || (player != null && player.getAbilities().instabuild);
    }

    public static WaystoneEditPermissions mayEditWaystone(Player player, Level world, IWaystone waystone) {
        if (WaystonesConfig.getActive().restrictions.restrictToCreative && !player.getAbilities().instabuild) {
            return WaystoneEditPermissions.NOT_CREATIVE;
        }

        if (WaystonesConfig.getActive().restrictions.restrictRenameToOwner && !waystone.isOwner(player)) {
            return WaystoneEditPermissions.NOT_THE_OWNER;
        }

        if (waystone.isGlobal() && !player.getAbilities().instabuild && WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode) {
            return WaystoneEditPermissions.GET_CREATIVE;
        }

        return WaystoneEditPermissions.ALLOW;
    }

    public static boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return getPlayerWaystoneData(player.level()).isWaystoneActivated(player, waystone);
    }

    public static void activateWaystone(Player player, IWaystone waystone) {
        if (!waystone.hasName() && waystone instanceof IMutableWaystone && waystone.wasGenerated()) {
            NameGenerationMode nameGenerationMode = WaystonesConfig.getActive().worldGen.nameGenerationMode;
            String name = NameGenerator.get(player.getServer()).getName(waystone, player.level().random, nameGenerationMode);
            ((IMutableWaystone) waystone).setName(name);
        }

        if (!waystone.hasOwner() && waystone instanceof IMutableWaystone) {
            ((IMutableWaystone) waystone).setOwnerUid(player.getUUID());
        }

        if (player.getServer() != null) {
            WaystoneManager.get(player.getServer()).setDirty();
        }

        if (!isWaystoneActivated(player, waystone) && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            getPlayerWaystoneData(player.level()).activateWaystone(player, waystone);

            Balm.getEvents().fireEvent(new WaystoneActivatedEvent(player, waystone));
        }
    }

    public static int predictExperienceLevelCost(Entity player, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        WaystoneTeleportContext context = new WaystoneTeleportContext(player, waystone, null);
        context.getLeashedEntities().addAll(findLeashedAnimals(player));
        context.setFromWaystone(fromWaystone);
        return getExperienceLevelCost(player, waystone, warpMode, context);
    }

    public static int getExperienceLevelCost(Entity entity, IWaystone waystone, WarpMode warpMode, IWaystoneTeleportContext context) {
        if (!(entity instanceof Player player)) {
            return 0;
        }

        if (context.getFromWaystone() != null && waystone.getWaystoneUid().equals(context.getFromWaystone().getWaystoneUid())) {
            return 0;
        }

        boolean enableXPCost = !player.getAbilities().instabuild;

        int xpForLeashed = WaystonesConfig.getActive().xpCost.xpCostPerLeashed * context.getLeashedEntities().size();

        double xpCostMultiplier = warpMode.getXpCostMultiplier();
        if (waystone.isGlobal()) {
            xpCostMultiplier *= WaystonesConfig.getActive().xpCost.globalWaystoneXpCostMultiplier;
        }

        BlockPos pos = waystone.getPos();
        double dist = Math.sqrt(player.distanceToSqr(pos.getX(), player.getY(), pos.getZ())); // ignore y distance
        final double minimumXpCost = WaystonesConfig.getActive().xpCost.minimumBaseXpCost;
        final double maximumXpCost = WaystonesConfig.getActive().xpCost.maximumBaseXpCost;
        double xpLevelCost;
        if (waystone.getDimension() != player.level().dimension()) {
            int dimensionalWarpXpCost = WaystonesConfig.getActive().xpCost.dimensionalWarpXpCost;
            xpLevelCost = Mth.clamp(dimensionalWarpXpCost, minimumXpCost, dimensionalWarpXpCost);
        } else if (WaystonesConfig.getActive().xpCost.blocksPerXpLevel > 0) {
            xpLevelCost = Mth.clamp(Math.floor(dist / (float) WaystonesConfig.getActive().xpCost.blocksPerXpLevel), minimumXpCost, maximumXpCost);

            if (WaystonesConfig.getActive().xpCost.inverseXpCost) {
                xpLevelCost = maximumXpCost - xpLevelCost;
            }
        } else {
            xpLevelCost = minimumXpCost;
        }

        return enableXPCost ? (int) Math.round((xpLevelCost + xpForLeashed) * xpCostMultiplier) : 0;
    }

    @Nullable
    public static IWaystone getInventoryButtonWaystone(Player player) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (inventoryButtonMode.isReturnToNearest()) {
            return PlayerWaystoneManager.getNearestWaystone(player);
        } else if (inventoryButtonMode.hasNamedTarget()) {
            return WaystoneManager.get(player.getServer()).findWaystoneByName(inventoryButtonMode.getNamedTarget()).orElse(null);
        }

        return null;
    }

    public static boolean canUseInventoryButton(Player player) {
        IWaystone waystone = getInventoryButtonWaystone(player);
        int xpLevelCost = waystone != null ? predictExperienceLevelCost(player, waystone, WarpMode.INVENTORY_BUTTON, null) : 0;
        return getInventoryButtonCooldownLeft(player) <= 0 && (xpLevelCost <= 0 || player.experienceLevel >= xpLevelCost);
    }

    public static boolean canUseWarpStone(Player player, ItemStack heldItem) {
        return getWarpStoneCooldownLeft(player) <= 0;
    }

    public static double getCooldownMultiplier(IWaystone waystone) {
        return waystone.isGlobal() ? WaystonesConfig.getActive().cooldowns.globalWaystoneCooldownMultiplier : 1f;
    }

    private static void informPlayer(Entity entity, String translationKey) {
        if (entity instanceof Player) {
            var chatComponent = Component.translatable(translationKey);
            chatComponent.withStyle(ChatFormatting.RED);
            ((Player) entity).displayClientMessage(chatComponent, false);
        }
    }

    public static Consumer<WaystoneTeleportError> informRejectedTeleport(final Entity entityToInform) {
        return error -> {
            logger.info("Rejected teleport: " + error.getClass().getSimpleName());
            if (error.getTranslationKey() != null) {
                informPlayer(entityToInform, error.getTranslationKey());
            }
        };
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleportToWaystone(Entity entity, IWaystone waystone, WarpMode warpMode, @Nullable IWaystone fromWaystone) {
        return WaystonesAPI.createDefaultTeleportContext(entity, waystone, warpMode, fromWaystone)
                .flatMap(PlayerWaystoneManager::tryTeleport)
                .ifRight(informRejectedTeleport(entity));
    }

    public static Either<List<Entity>, WaystoneTeleportError> tryTeleport(IWaystoneTeleportContext context) {
        WaystoneTeleportEvent.Pre event = new WaystoneTeleportEvent.Pre(context);
        Balm.getEvents().fireEvent(event);
        if (event.isCanceled()) {
            return Either.right(new WaystoneTeleportError.CancelledByEvent());
        }

        final var waystone = context.getTargetWaystone();
        final var entity = context.getEntity();
        final var warpMode = context.getWarpMode();
        if (!canUseWarpMode(entity, warpMode, context.getWarpItem(), context.getFromWaystone())) {
            return Either.right(new WaystoneTeleportError.WarpModeRejected());
        }

        if (!warpMode.getAllowTeleportPredicate().test(entity, waystone)) {
            return Either.right(new WaystoneTeleportError.WarpModeRejected());
        }

        if (context.isDimensionalTeleport() && !event.getDimensionalTeleportResult().withDefault(() -> canDimensionalWarpBetween(entity, waystone))) {
            return Either.right(new WaystoneTeleportError.DimensionalWarpDenied());
        }

        if (!context.getLeashedEntities().isEmpty()) {
            if (!WaystonesConfig.getActive().restrictions.transportLeashed) {
                return Either.right(new WaystoneTeleportError.LeashedWarpDenied());
            }

            List<ResourceLocation> forbidden = WaystonesConfig.getActive().restrictions.leashedDenyList.stream().map(ResourceLocation::new).toList();
            if (context.getLeashedEntities().stream().anyMatch(e -> forbidden.contains(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType())))) {
                return Either.right(new WaystoneTeleportError.SpecificLeashedWarpDenied());
            }

            if (context.isDimensionalTeleport() && !WaystonesConfig.getActive().restrictions.transportLeashedDimensional) {
                return Either.right(new WaystoneTeleportError.LeashedDimensionalWarpDenied());
            }
        }

        if (entity instanceof Player && ((Player) entity).experienceLevel < context.getXpCost()) {
            return Either.right(new WaystoneTeleportError.NotEnoughXp());
        }

        boolean isCreativeMode = entity instanceof Player && ((Player) entity).getAbilities().instabuild;
        if (!context.getWarpItem().isEmpty() && event.getConsumeItemResult().withDefault(() -> !isCreativeMode && context.consumesWarpItem())) {
            context.getWarpItem().shrink(1);
        }

        if (entity instanceof Player player) {
            applyCooldown(warpMode, player, context.getCooldown());
            applyXpCost(player, context.getXpCost());
        }

        final var teleportedEntities = doTeleport(context);

        Balm.getEvents().fireEvent(new WaystoneTeleportEvent.Post(context, teleportedEntities));

        return Either.left(teleportedEntities);
    }

    private static void sendHackySyncPacketsAfterTeleport(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            // No idea why this is still needed since we're using the same code as /tp. Maybe /tp is broken too for interdimensional travel.
            player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        }
    }

    private static void applyXpCost(Player player, int xpLevelCost) {
        if (xpLevelCost > 0) {
            player.giveExperienceLevels(-xpLevelCost);
        }
    }

    private static void applyCooldown(WarpMode warpMode, Player player, int cooldown) {
        if (cooldown > 0) {
            final Level level = player.level();
            switch (warpMode) {
                case INVENTORY_BUTTON ->
                        getPlayerWaystoneData(level).setInventoryButtonCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000L);
                case WARP_STONE -> getPlayerWaystoneData(level).setWarpStoneCooldownUntil(player, System.currentTimeMillis() + cooldown * 1000L);
            }
            WaystoneSyncManager.sendWaystoneCooldowns(player);
        }
    }

    public static int getCooldownPeriod(WarpMode warpMode, IWaystone waystone) {
        return (int) (getCooldownPeriod(warpMode) * getCooldownMultiplier(waystone));
    }

    private static int getCooldownPeriod(WarpMode warpMode) {
        return switch (warpMode) {
            case INVENTORY_BUTTON -> WaystonesConfig.getActive().cooldowns.inventoryButtonCooldown;
            case WARP_STONE -> WaystonesConfig.getActive().cooldowns.warpStoneCooldown;
            default -> 0;
        };
    }

    private static boolean canDimensionalWarpBetween(Entity player, IWaystone waystone) {
        ResourceLocation fromDimension = player.level().dimension().location();
        ResourceLocation toDimension = waystone.getDimension().location();
        Collection<String> dimensionAllowList = WaystonesConfig.getActive().restrictions.dimensionalWarpAllowList;
        Collection<String> dimensionDenyList = WaystonesConfig.getActive().restrictions.dimensionalWarpDenyList;
        if (!dimensionAllowList.isEmpty() && (!dimensionAllowList.contains(toDimension.toString()) || !dimensionAllowList.contains(fromDimension.toString()))) {
            return false;
        } else if (!dimensionDenyList.isEmpty() && (dimensionDenyList.contains(toDimension.toString()) || dimensionDenyList.contains(fromDimension.toString()))) {
            return false;
        }

        DimensionalWarp dimensionalWarpMode = WaystonesConfig.getActive().restrictions.dimensionalWarp;
        return dimensionalWarpMode == DimensionalWarp.ALLOW || dimensionalWarpMode == DimensionalWarp.GLOBAL_ONLY && waystone.isGlobal();
    }

    public static ItemStack findWarpItem(Entity entity, WarpMode warpMode) {
        return switch (warpMode) {
            case WARP_SCROLL -> findWarpItem(entity, ModItemTags.WARP_SCROLLS);
            case WARP_STONE -> findWarpItem(entity, ModItemTags.WARP_STONES);
            case RETURN_SCROLL -> findWarpItem(entity, ModItemTags.RETURN_SCROLLS);
            case BOUND_SCROLL -> findWarpItem(entity, ModItemTags.BOUND_SCROLLS);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack findWarpItem(Entity entity, TagKey<Item> warpItemTag) {
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getMainHandItem().is(warpItemTag)) {
                return livingEntity.getMainHandItem();
            } else if (livingEntity.getOffhandItem().is(warpItemTag)) {
                return livingEntity.getOffhandItem();
            }
        }

        return ItemStack.EMPTY;
    }

    public static List<Mob> findLeashedAnimals(Entity player) {
        return player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(10),
                e -> player.equals(e.getLeashHolder())
        );
    }

    public static List<Entity> doTeleport(IWaystoneTeleportContext context) {
        List<Entity> teleportedEntities = teleportEntityAndAttached(context.getEntity(), context);
        context.getAdditionalEntities().forEach(additionalEntity -> teleportedEntities.addAll(teleportEntityAndAttached(additionalEntity, context)));

        ServerLevel sourceWorld = (ServerLevel) context.getEntity().level();
        BlockPos sourcePos = context.getEntity().blockPosition();

        final var destination = context.getDestination();
        final var targetLevel = destination.getLevel();
        final var targetPos = BlockPos.containing(destination.getLocation());

        BlockEntity targetTileEntity = targetLevel.getBlockEntity(targetPos);
        if (targetTileEntity instanceof WarpPlateBlockEntity warpPlate) {
            teleportedEntities.forEach(warpPlate::markEntityForCooldown);
        }

        if (context.playsSound()) {
            sourceWorld.playSound(null, sourcePos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.1f, 1f);
            targetLevel.playSound(null, targetPos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.1f, 1f);
        }

        if (context.playsEffect()) {
            teleportedEntities.forEach(additionalEntity -> Balm.getNetworking().sendToTracking(sourceWorld, sourcePos, new TeleportEffectMessage(sourcePos)));
            Balm.getNetworking().sendToTracking(targetLevel, targetPos, new TeleportEffectMessage(targetPos));
        }

        return teleportedEntities;
    }

    private static List<Entity> teleportEntityAndAttached(Entity entity, IWaystoneTeleportContext context) {
        final var teleportedEntities = new ArrayList<Entity>();

        final var destination = context.getDestination();
        final var targetLevel = destination.getLevel();
        final var targetLocation = destination.getLocation();
        final var targetDirection = destination.getDirection();

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
        if (entity instanceof ServerPlayer) {
            ChunkPos chunkPos = new ChunkPos(BlockPos.containing(x, y, z));
            targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (((ServerPlayer) entity).isSleeping()) {
                ((ServerPlayer) entity).stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level()) {
                ((ServerPlayer) entity).connection.teleport(x, y, z, yaw, entity.getXRot(), Collections.emptySet());
            } else {
                ((ServerPlayer) entity).teleportTo(targetWorld, x, y, z, yaw, entity.getXRot());
            }

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level()) {
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
            } else {
                entity.unRide();
                Entity oldEntity = entity;
                entity = entity.getType().create(targetWorld);
                if (entity == null) {
                    return oldEntity;
                }

                entity.restoreFrom(oldEntity);
                entity.moveTo(x, y, z, yaw, pitch);
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

    public static void deactivateWaystone(Player player, IWaystone waystone) {
        getPlayerWaystoneData(player.level()).deactivateWaystone(player, waystone);
    }

    private static boolean canUseWarpMode(Entity entity, WarpMode warpMode, ItemStack heldItem, @Nullable IWaystone fromWaystone) {
        return switch (warpMode) {
            case INVENTORY_BUTTON -> entity instanceof Player && PlayerWaystoneManager.canUseInventoryButton(((Player) entity));
            case WARP_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.WARP_SCROLLS);
            case BOUND_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.BOUND_SCROLLS);
            case RETURN_SCROLL -> !heldItem.isEmpty() && heldItem.is(ModItemTags.RETURN_SCROLLS);
            case WARP_STONE -> !heldItem.isEmpty() && heldItem.is(ModItemTags.WARP_STONES) && entity instanceof Player
                    && PlayerWaystoneManager.canUseWarpStone(((Player) entity), heldItem);
            case WAYSTONE_TO_WAYSTONE -> WaystonesConfig.getActive()
                    .restrictions.allowWaystoneToWaystoneTeleport && fromWaystone != null && fromWaystone.isValid()
                    && fromWaystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE);
            case SHARESTONE_TO_SHARESTONE -> fromWaystone != null && fromWaystone.isValid() && WaystoneTypes.isSharestone(fromWaystone.getWaystoneType());
            case WARP_PLATE -> fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE);
            case PORTSTONE_TO_WAYSTONE -> fromWaystone != null && fromWaystone.isValid() && fromWaystone.getWaystoneType().equals(WaystoneTypes.PORTSTONE);
            case CUSTOM -> true;
        };

    }

    public static long getWarpStoneCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level()).getWarpStoneCooldownUntil(player);
    }

    public static long getWarpStoneCooldownLeft(Player player) {
        long cooldownUntil = getWarpStoneCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level()).setWarpStoneCooldownUntil(player, timeStamp);
    }

    public static long getInventoryButtonCooldownUntil(Player player) {
        return getPlayerWaystoneData(player.level()).getInventoryButtonCooldownUntil(player);
    }

    public static long getInventoryButtonCooldownLeft(Player player) {
        long cooldownUntil = getInventoryButtonCooldownUntil(player);
        return Math.max(0, cooldownUntil - System.currentTimeMillis());
    }

    public static void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        getPlayerWaystoneData(player.level()).setInventoryButtonCooldownUntil(player, timeStamp);
    }

    @Nullable
    public static IWaystone getNearestWaystone(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player).stream()
                .filter(it -> it.getDimension() == player.level().dimension())
                .min((first, second) -> {
                    double firstDist = first.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    double secondDist = second.getPos().distToCenterSqr(player.getX(), player.getY(), player.getZ());
                    return (int) Math.round(firstDist) - (int) Math.round(secondDist);
                }).orElse(null);
    }

    public static List<IWaystone> getWaystones(Player player) {
        return getPlayerWaystoneData(player.level()).getWaystones(player);
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(@Nullable Level world) {
        return world == null || world.isClientSide ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static IPlayerWaystoneData getPlayerWaystoneData(BalmEnvironment side) {
        return side.isClient() ? inMemoryPlayerWaystoneData : persistentPlayerWaystoneData;
    }

    public static boolean mayTeleportToWaystone(Player player, IWaystone waystone) {
        return true;
    }

    public static void swapWaystoneSorting(Player player, int index, int otherIndex) {
        getPlayerWaystoneData(player.level()).swapWaystoneSorting(player, index, otherIndex);
    }

    public static boolean mayEditGlobalWaystones(Player player) {
        return player.getAbilities().instabuild || !WaystonesConfig.getActive().restrictions.globalWaystoneSetupRequiresCreativeMode;
    }

    public static void activeWaystoneForEveryone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (!isWaystoneActivated(player, waystone)) {
                activateWaystone(player, waystone);
            }
        }
    }

    public static void removeKnownWaystone(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            deactivateWaystone(player, waystone);
            WaystoneSyncManager.sendActivatedWaystones(player);
        }
    }

}
