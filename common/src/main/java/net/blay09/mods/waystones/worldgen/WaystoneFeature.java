package net.blay09.mods.waystones.worldgen;

import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class WaystoneFeature extends Feature<BlockStateConfiguration> {

    public WaystoneFeature() {
        super(BlockStateConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        Direction facing = Direction.values()[2 + random.nextInt(4)];
        BlockState state = world.getBlockState(pos);
        BlockPos posAbove = pos.above();
        BlockState stateAbove = world.getBlockState(posAbove);
        if (state.isAir() && stateAbove.isAir()) {
            world.setBlock(pos, context.config().state
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                    .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.WILDERNESS)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            world.setBlock(posAbove, context.config().state
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.WILDERNESS)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            WaystoneBlockEntity tileEntity = (WaystoneBlockEntity) world.getBlockEntity(pos);
            if (tileEntity != null) {
                tileEntity.initializeWaystone(world, null, WaystoneOrigin.WILDERNESS);

                BlockEntity tileEntityAbove = world.getBlockEntity(pos.above());
                if (tileEntityAbove instanceof WaystoneBlockEntity) {
                    ((WaystoneBlockEntity) tileEntityAbove).initializeFromBase(tileEntity);
                }
            }

            return true;
        }

        return false;
    }

}
