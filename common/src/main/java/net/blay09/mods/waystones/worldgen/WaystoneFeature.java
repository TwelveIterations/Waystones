package net.blay09.mods.waystones.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public record WaystoneFeature(BlockState state) implements Feature {

    public static final MapCodec<WaystoneFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockState.CODEC.fieldOf("state").forGetter(WaystoneFeature::state)).apply(i, WaystoneFeature::new));

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
        Direction facing = Direction.values()[2 + random.nextInt(4)];
        BlockState currentState = level.getBlockState(pos);
        BlockPos posAbove = pos.above();
        BlockState currentStateAbove = level.getBlockState(posAbove);
        if (currentState.isAir() && currentStateAbove.isAir()) {
            level.setBlock(pos, state
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.LOWER)
                    .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.WILDERNESS)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            level.setBlock(posAbove, state
                    .setValue(WaystoneBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(WaystoneBlockBase.ORIGIN, WaystoneOrigin.WILDERNESS)
                    .setValue(WaystoneBlock.FACING, facing), 2);

            WaystoneBlockEntity tileEntity = (WaystoneBlockEntity) level.getBlockEntity(pos);
            if (tileEntity != null) {
                tileEntity.initializeWaystone(level, null, WaystoneOrigin.WILDERNESS);

                BlockEntity tileEntityAbove = level.getBlockEntity(pos.above());
                if (tileEntityAbove instanceof WaystoneBlockEntity) {
                    ((WaystoneBlockEntity) tileEntityAbove).initializeFromBase(tileEntity);
                }
            }

            return true;
        }

        return false;
    }

}
