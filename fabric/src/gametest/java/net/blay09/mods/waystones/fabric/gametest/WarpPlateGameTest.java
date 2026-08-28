package net.blay09.mods.waystones.fabric.gametest;

import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class WarpPlateGameTest {

    @GameTest(maxTicks = 100)
    public void itemTeleportsOnceAndRemainsAtTarget(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = setWarpPlate(helper, sourcePos);
        final var targetPos = new BlockPos(6, 1, 2);
        final var targetPlate = setWarpPlate(helper, targetPos);
        linkWarpPlates(sourcePlate, targetPlate);

        final var itemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntity.setDeltaMovement(Vec3.ZERO);

        helper.assertItemEntityPresent(Items.DIAMOND, sourcePos, 1);
        helper.assertItemEntityNotPresent(Items.DIAMOND, targetPos, 1);

        helper.runAtTickTime(35, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityType.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
        });
        helper.runAtTickTime(75, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityType.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            helper.succeed();
        });
    }

    @GameTest(maxTicks = 100)
    public void itemStackTeleportsOnceAndRemainsAtTarget(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = setWarpPlate(helper, sourcePos);
        final var targetPos = new BlockPos(6, 1, 2);
        final var targetPlate = setWarpPlate(helper, targetPos);
        linkWarpPlates(sourcePlate, targetPlate);

        final var itemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntity.setItem(new ItemStack(Items.DIAMOND, 32));
        itemEntity.setDeltaMovement(Vec3.ZERO);

        helper.assertItemEntityPresent(Items.DIAMOND, sourcePos, 1);
        helper.assertItemEntityNotPresent(Items.DIAMOND, targetPos, 1);

        helper.runAtTickTime(35, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityType.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
        });
        helper.runAtTickTime(75, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityType.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            helper.succeed();
        });
    }

    private static void linkWarpPlates(WarpPlateBlockEntity sourcePlate, WarpPlateBlockEntity targetPlate) {
        sourcePlate.setShardItem(WaystonesAPI.createAttunedShard(targetPlate.getWaystone()));
        targetPlate.setShardItem(WaystonesAPI.createAttunedShard(sourcePlate.getWaystone()));
    }

    private static WarpPlateBlockEntity setWarpPlate(GameTestHelper helper, BlockPos sourcePos) {
        helper.setBlock(sourcePos, ModBlocks.warpPlate.value());
        final var warpPlate = helper.getBlockEntity(sourcePos, WarpPlateBlockEntity.class);
        warpPlate.initializeWaystone(helper.getLevel(), null, WaystoneOrigin.PLAYER);
        return warpPlate;
    }

}
