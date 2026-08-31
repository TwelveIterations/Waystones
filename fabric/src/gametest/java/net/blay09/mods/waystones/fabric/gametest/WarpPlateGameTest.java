package net.blay09.mods.waystones.fabric.gametest;

import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.core.WaystoneTeleportedEntity;
import net.blay09.mods.waystones.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class WarpPlateGameTest {

    @GameTest(maxTicks = 50)
    public void initialDormantShardIsAttunedAndPoppedOut(GameTestHelper helper) {
        final var warpPlatePos = new BlockPos(2, 1, 2);
        WaystonesTestHelper.setWarpPlate(helper, warpPlatePos);

        helper.assertBlockEntityData(warpPlatePos, WarpPlateBlockEntity.class,
                warpPlate -> warpPlate.getShardItem().is(ModItems.dormantShard.asItem()),
                () -> Component.literal("Warp Plate should start with a dormant shard"));
        helper.assertItemEntityNotPresent(ModItems.attunedShard.asItem(), warpPlatePos, 1);

        helper.runAtTickTime(35, () -> {
            helper.assertBlockEntityData(warpPlatePos, WarpPlateBlockEntity.class,
                    warpPlate -> warpPlate.getShardItem().isEmpty(),
                    () -> Component.literal("Warp Plate should eject its attuned shard"));
            helper.assertItemEntityCountIs(ModItems.attunedShard.asItem(), warpPlatePos, 1, 1);
            helper.succeed();
        });
    }

    @GameTest(maxTicks = 100)
    public void itemTeleportsOnceAndRemainsAtTarget(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = WaystonesTestHelper.setWarpPlate(helper, sourcePos);
        final var targetPos = new BlockPos(6, 1, 2);
        final var targetPlate = WaystonesTestHelper.setWarpPlate(helper, targetPos);
        WaystonesTestHelper.linkWarpPlates(sourcePlate, targetPlate);

        final var itemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntity.setDeltaMovement(Vec3.ZERO);

        helper.assertItemEntityPresent(Items.DIAMOND, sourcePos, 1);
        helper.assertItemEntityNotPresent(Items.DIAMOND, targetPos, 1);

        helper.runAtTickTime(35, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityTypes.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
        });
        helper.runAtTickTime(75, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityTypes.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            helper.succeed();
        });
    }

    @GameTest(environment = "waystones-test:chunk_loading", maxTicks = 200)
    public void singleItemTeleportsToUnloadedChunk(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = WaystonesTestHelper.setWarpPlate(helper, sourcePos);

        final var targetPos = new BlockPos(2008, 1, 2008);
        final var targetChunk = ChunkPos.containing(helper.absolutePos(targetPos));
        final var targetPlate = WaystonesTestHelper.setWarpPlate(helper, targetPos);
        WaystonesTestHelper.linkWarpPlates(sourcePlate, targetPlate);
        WaystonesTestHelper.unloadChunk(helper, targetChunk);

        final var itemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntity.setDeltaMovement(Vec3.ZERO);
        helper.assertItemEntityPresent(Items.DIAMOND, sourcePos, 1);

        helper.succeedWhen(() -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            helper.assertTrue(targetPlate.getWaystone().getWaystoneUid().equals(((WaystoneTeleportedEntity) itemEntity).waystones$getLastWarpPlate()),
                    "Teleported entity should track the target Warp Plate");
        });
    }

    @GameTest(environment = "waystones-test:chunk_loading", maxTicks = 200)
    public void multipleItemsSpawnedInSeparateTicksTeleportToUnloadedChunk(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = WaystonesTestHelper.setWarpPlate(helper, sourcePos);

        final var targetPos = new BlockPos(2008, 1, 2008);
        final var targetChunk = ChunkPos.containing(helper.absolutePos(targetPos));
        final var targetPlate = WaystonesTestHelper.setWarpPlate(helper, targetPos);
        WaystonesTestHelper.linkWarpPlates(sourcePlate, targetPlate);
        WaystonesTestHelper.unloadChunk(helper, targetChunk);

        final var itemEntities = new Entity[3];
        itemEntities[0] = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntities[0].setDeltaMovement(Vec3.ZERO);

        helper.runAtTickTime(1, () -> {
            itemEntities[1] = helper.spawnItem(Items.EMERALD, Vec3.atCenterOf(sourcePos));
            itemEntities[1].setDeltaMovement(Vec3.ZERO);
        });
        helper.runAtTickTime(2, () -> {
            itemEntities[2] = helper.spawnItem(Items.GOLD_INGOT, Vec3.atCenterOf(sourcePos));
            itemEntities[2].setDeltaMovement(Vec3.ZERO);
        });

        helper.runAtTickTime(3, () -> helper.succeedWhen(() -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertItemEntityNotPresent(Items.EMERALD, sourcePos, 1);
            helper.assertItemEntityNotPresent(Items.GOLD_INGOT, sourcePos, 1);
            for (final var itemEntity : itemEntities) {
                helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            }
        }));
    }

    @GameTest(maxTicks = 100)
    public void itemStackTeleportsOnceAndRemainsAtTarget(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = WaystonesTestHelper.setWarpPlate(helper, sourcePos);
        final var targetPos = new BlockPos(6, 1, 2);
        final var targetPlate = WaystonesTestHelper.setWarpPlate(helper, targetPos);
        WaystonesTestHelper.linkWarpPlates(sourcePlate, targetPlate);

        final var itemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        itemEntity.setItem(new ItemStack(Items.DIAMOND, 32));
        itemEntity.setDeltaMovement(Vec3.ZERO);

        helper.assertItemEntityPresent(Items.DIAMOND, sourcePos, 1);
        helper.assertItemEntityNotPresent(Items.DIAMOND, targetPos, 1);

        helper.runAtTickTime(35, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityTypes.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
        });
        helper.runAtTickTime(75, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertEntitiesPresent(EntityTypes.ITEM, 1);
            helper.assertEntityInstancePresent(itemEntity, targetPos, 1);
            helper.succeed();
        });
    }

    @GameTest(maxTicks = 100)
    public void mergedItemsTeleportOnceAndRemainAtTarget(GameTestHelper helper) {
        final var sourcePos = new BlockPos(2, 1, 2);
        final var sourcePlate = WaystonesTestHelper.setWarpPlate(helper, sourcePos);
        final var targetPos = new BlockPos(6, 1, 2);
        final var targetPlate = WaystonesTestHelper.setWarpPlate(helper, targetPos);
        WaystonesTestHelper.linkWarpPlates(sourcePlate, targetPlate);

        final var firstItemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        firstItemEntity.setDeltaMovement(Vec3.ZERO);
        firstItemEntity.tickCount = 39;
        final var secondItemEntity = helper.spawnItem(Items.DIAMOND, Vec3.atCenterOf(sourcePos));
        secondItemEntity.setDeltaMovement(Vec3.ZERO);
        secondItemEntity.tickCount = 39;

        helper.assertItemEntityCountIs(Items.DIAMOND, sourcePos, 1, 2);
        helper.assertEntitiesPresent(EntityTypes.ITEM, sourcePos, 2, 1);
        helper.assertItemEntityNotPresent(Items.DIAMOND, targetPos, 1);

        helper.runAtTickTime(1, () -> {
            helper.assertItemEntityCountIs(Items.DIAMOND, sourcePos, 1, 2);
            helper.assertEntitiesPresent(EntityTypes.ITEM, sourcePos, 1, 1);
            helper.assertEntityData(sourcePos, EntityTypes.ITEM, itemEntity -> itemEntity.getItem().getCount() == 2);
        });
        helper.runAtTickTime(35, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertItemEntityCountIs(Items.DIAMOND, targetPos, 1, 2);
            helper.assertEntitiesPresent(EntityTypes.ITEM, targetPos, 1, 1);
            helper.assertEntityData(targetPos, EntityTypes.ITEM, itemEntity -> itemEntity.getItem().getCount() == 2);
        });
        helper.runAtTickTime(75, () -> {
            helper.assertItemEntityNotPresent(Items.DIAMOND, sourcePos, 1);
            helper.assertItemEntityCountIs(Items.DIAMOND, targetPos, 1, 2);
            helper.assertEntitiesPresent(EntityTypes.ITEM, targetPos, 1, 1);
            helper.assertEntityData(targetPos, EntityTypes.ITEM, itemEntity -> itemEntity.getItem().getCount() == 2);
            helper.succeed();
        });
    }

}
