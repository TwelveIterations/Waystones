package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.MutableWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.event.WaystoneTeleportEvent;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.FleetingMemorialBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FleetingMemorialManager {

    private static final int SEARCH_RADIUS = 2;
    private static final int SEARCH_HEIGHT = 3;

    public static boolean spawnMemorial(ServerPlayer player) {
        final var level = player.serverLevel();
        final var pos = findMemorialPos(level, player.blockPosition());
        if (pos == null) {
            return false;
        }

        final var state = ModBlocks.fleetingMemorial.defaultBlockState()
                .setValue(WaystoneBlockBase.FACING, Direction.fromYRot(player.getYRot()).getOpposite())
                .setValue(WaystoneBlockBase.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        level.setBlock(pos, state, Block.UPDATE_ALL);

        if (level.getBlockEntity(pos) instanceof WaystoneBlockEntityBase blockEntity) {
            blockEntity.initializeWaystone(level, player, WaystoneOrigin.PLAYER);
            if (blockEntity instanceof FleetingMemorialBlockEntity fleetingMemorialBlockEntity) {
                fleetingMemorialBlockEntity.setOwnerName(player.getDisplayName());
            }
            if (blockEntity.getWaystone() instanceof MutableWaystone mutableWaystone) {
                mutableWaystone.setName(Component.translatable("block.waystones.fleeting_memorial"));
                WaystoneManagerImpl.get(level.getServer()).updateWaystone(blockEntity.getWaystone());
            }
            WaystoneSyncManager.sendWaystoneUpdate(player, blockEntity.getWaystone());
            return true;
        }

        level.removeBlock(pos, false);
        return false;
    }

    public static Collection<Waystone> getTargets(ServerPlayer player) {
        final var server = player.level().getServer();
        return WaystoneManagerImpl.get(server)
                .getWaystonesByType(WaystoneTypes.FLEETING_MEMORIAL)
                .filter(waystone -> player.getUUID().equals(waystone.getOwnerUid()))
                .filter(waystone -> server.getLevel(waystone.getDimension()) != null)
                .toList();
    }

    public static void removeAfterUse(ServerLevel sourceLevel, Waystone waystone) {
        final var server = sourceLevel.getServer();
        if (!(server.getLevel(waystone.getDimension()) instanceof ServerLevel level)) {
            return;
        }

        final var pos = waystone.getPos();
        if (level.getBlockState(pos).is(ModBlocks.fleetingMemorial)) {
            level.destroyBlock(pos, false);
        } else {
            WaystoneManagerImpl.get(server).removeWaystone(waystone);
            PlayerWaystoneManager.removeKnownWaystone(server, waystone);
            WaystoneSyncManager.sendWaystoneRemovalToAll(server, waystone, true);
        }
    }

    public static void handleTeleportAfter(WaystoneTeleportEvent.Post event) {
        if (event.getContext().getEntity().level() instanceof ServerLevel sourceLevel) {
            final var waystone = event.getContext().getTargetWaystone();
            if (WaystoneTypes.FLEETING_MEMORIAL.equals(waystone.getWaystoneType())) {
                removeAfterUse(sourceLevel, waystone);
            }
        }
    }

    private static @Nullable BlockPos findMemorialPos(ServerLevel level, BlockPos deathPos) {
        final var preferred = deathPos.above();
        if (canPlaceMemorialAt(level, preferred)) {
            return preferred;
        }

        for (int dy = 0; dy <= SEARCH_HEIGHT; dy++) {
            for (int radius = 1; radius <= SEARCH_RADIUS; radius++) {
                for (BlockPos candidate : BlockPos.betweenClosed(deathPos.offset(-radius, dy, -radius), deathPos.offset(radius, dy, radius))) {
                    if (canPlaceMemorialAt(level, candidate)) {
                        return candidate.immutable();
                    }
                }
            }
        }

        return null;
    }

    private static boolean canPlaceMemorialAt(ServerLevel level, BlockPos pos) {
        return level.isInWorldBounds(pos) && level.getBlockState(pos).canBeReplaced();
    }
}
