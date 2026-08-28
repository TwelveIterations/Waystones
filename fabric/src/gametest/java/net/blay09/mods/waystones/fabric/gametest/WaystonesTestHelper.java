package net.blay09.mods.waystones.fabric.gametest;

import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;

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
}
