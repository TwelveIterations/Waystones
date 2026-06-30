package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WarpPortalBlock;
import net.blay09.mods.waystones.block.entity.WarpPortalBlockEntity;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public class WarpPortalManager {

    private static final String RETURN_PORTAL_KEY = "ReturnPortal";
    private static final WeakHashMap<Entity, Integer> portalCooldowns = new WeakHashMap<>();

    public static boolean hasSpaceForPortal(Player player) {
        return findPortalPos(player).isPresent();
    }

    public static boolean spawnPortal(ServerPlayer player, Waystone targetWaystone) {
        final var portalPos = findPortalPos(player);
        if (portalPos.isEmpty()) {
            return false;
        }

        final var level = player.level();
        final var pos = portalPos.get();
        final var facing = Direction.fromYRot(player.getYRot()).getOpposite();
        level.setBlock(pos, ModBlocks.warpPortal.defaultBlockState()
                .setValue(WarpPortalBlock.FACING, facing)
                .setValue(WarpPortalBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), ModBlocks.warpPortal.defaultBlockState()
                .setValue(WarpPortalBlock.FACING, facing)
                .setValue(WarpPortalBlock.HALF, DoubleBlockHalf.UPPER), 3);

        if (level.getBlockEntity(pos) instanceof WarpPortalBlockEntity warpPortal) {
            warpPortal.initialize(level, targetWaystone);
        }

        level.playSound(null, pos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.25f, 1.4f);
        spawnPortalBurst(level, pos);
        return true;
    }

    private static void spawnPortalBurst(ServerLevel level, BlockPos pos) {
        final var x = pos.getX() + 0.5;
        final var y = pos.getY() + 1;
        final var z = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.PORTAL, x, y, z, 96, 0.6, 0.85, 0.6, 0.08);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 48, 0.45, 0.65, 0.45, 0.12);
    }

    private static Optional<BlockPos> findPortalPos(Player player) {
        final var level = player.level();
        final var look = player.getLookAngle();
        final var horizontal = new Vec3(look.x, 0, look.z).normalize();
        if (horizontal.lengthSqr() < 0.001) {
            return Optional.empty();
        }

        for (int distance = 3; distance <= 4; distance++) {
            final var center = player.position().add(horizontal.scale(distance));
            final var start = BlockPos.containing(center.x, player.getY() + 2, center.z);
            for (int dy = 0; dy <= 5; dy++) {
                final var base = start.below(dy);
                if (canPlacePortalAt(level, base)) {
                    return Optional.of(base);
                }
            }
        }

        return Optional.empty();
    }

    private static boolean canPlacePortalAt(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid()
                && level.getBlockState(pos).canBeReplaced()
                && level.getBlockState(pos.above()).canBeReplaced()
                && level.getBlockState(pos.above(2)).canBeReplaced();
    }

    public static boolean canUsePortal(Entity entity, WarpPortalBlockEntity portal) {
        final var cooldown = portalCooldowns.get(entity);
        if (cooldown != null && entity.tickCount - cooldown < 20) {
            return false;
        }

        portalCooldowns.put(entity, entity.tickCount);
        return !WaystonePermissionManager.isEntityDeniedTeleports(entity);
    }

    public static void teleportFromPortal(Entity entity, WarpPortalBlockEntity portal, Waystone targetWaystone) {
        WaystonesAPI.createUncheckedDefaultTeleportContext(entity, targetWaystone, it -> it.setFromWaystone(portal.getWaystone()))
                .ifLeft(context -> WaystonesAPI.tryTeleportAsync(context)
                        .thenAccept(result -> result
                                .ifLeft(teleportedEntities -> teleportedEntities.forEach(teleportedEntity -> setReturnPortal(teleportedEntity, portal.getWaystone())))
                                .ifRight(error -> {
                                    if (entity instanceof Player player) {
                                        player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED));
                                    }
                                })))
                .ifRight(error -> {
                    if (entity instanceof Player player) {
                        player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED));
                    }
                });
    }

    public static void setReturnPortal(Entity entity, Waystone portalWaystone) {
        if (!portalWaystone.isValid()) {
            return;
        }

        final var data = Balm.hooks().getPersistentData(entity);
        data.store(RETURN_PORTAL_KEY, UUIDUtil.CODEC, portalWaystone.getWaystoneUid());
    }

    public static Optional<Waystone> getReturnPortal(Player player) {
        final CompoundTag data = Balm.hooks().getPersistentData(player);
        return data.read(RETURN_PORTAL_KEY, UUIDUtil.CODEC)
                .flatMap(uuid -> findValidWaystone(player, uuid));
    }

    private static Optional<Waystone> findValidWaystone(Player player, UUID waystoneUid) {
        final var server = player.level().getServer();
        if (server == null) {
            return Optional.empty();
        }

        final var waystone = new WaystoneProxy(server, waystoneUid);
        if (waystone.isValid() && server.getLevel(waystone.getDimension()) instanceof ServerLevel level && waystone.isValidInLevel(level)) {
            return Optional.of(waystone);
        }

        SavedDataWaystonesStore.get(server).removeWaystone(waystone);
        return Optional.empty();
    }
}
