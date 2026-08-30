package net.blay09.mods.waystones.fabric.gametest;

import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.fabric.gametest.mixin.ChunkMapAccessor;
import net.blay09.mods.waystones.fabric.gametest.mixin.ServerChunkCacheAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public class WaystonesTestHelper {

    public static void linkWarpPlates(WarpPlateBlockEntity sourcePlate, WarpPlateBlockEntity targetPlate) {
        sourcePlate.setShardItem(WaystonesAPI.createAttunedShard(targetPlate.getWaystone()));
        targetPlate.setShardItem(WaystonesAPI.createAttunedShard(sourcePlate.getWaystone()));
    }

    public static WarpPlateBlockEntity setWarpPlate(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, ModBlocks.warpPlate.value());
        final var warpPlate = helper.getBlockEntity(pos, WarpPlateBlockEntity.class);
        warpPlate.initializeWaystone(helper.getLevel(), null, WaystoneOrigin.PLAYER);
        return warpPlate;
    }

    public static void unloadChunk(GameTestHelper helper, ChunkPos chunkPos) {
        final var level = helper.getLevel();
        final var chunkSource = level.getChunkSource();
        chunkSource.save(true);

        final var chunkSourceInvoker = (ServerChunkCacheAccessor) chunkSource;
        final var ticketStorage = chunkSourceInvoker.getTicketStorage();
        ticketStorage.removeTicketIf((_, key) -> key == chunkPos.pack(), null);

        chunkSourceInvoker.callRunDistanceManagerUpdates();
        final var chunkMap = (ChunkMapAccessor) chunkSource.chunkMap;
        chunkMap.callProcessUnloads(() -> true);

        helper.assertFalse(chunkSource.hasChunk(chunkPos.x(), chunkPos.z()), "Target chunk failed to unload");
    }
}
