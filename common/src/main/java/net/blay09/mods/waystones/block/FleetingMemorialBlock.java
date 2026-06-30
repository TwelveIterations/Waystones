package net.blay09.mods.waystones.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.FleetingMemorialBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FleetingMemorialBlock extends WaystoneBlockBase {

    public static final MapCodec<FleetingMemorialBlock> CODEC = simpleCodec(FleetingMemorialBlock::new);
    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            box(3.0, 2.0, 8.0, 13.0, 14.0, 9.0),
            box(5.0, 1.0, 8.0, 12.0, 2.0, 9.0),
            box(4.0, 14.0, 8.0, 11.0, 15.0, 9.0)
    ).optimize();
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            box(3.0, 2.0, 7.0, 13.0, 14.0, 8.0),
            box(4.0, 1.0, 7.0, 11.0, 2.0, 8.0),
            box(5.0, 14.0, 7.0, 12.0, 15.0, 8.0)
    ).optimize();
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            box(7.0, 2.0, 3.0, 8.0, 14.0, 13.0),
            box(7.0, 1.0, 5.0, 8.0, 2.0, 12.0),
            box(7.0, 14.0, 4.0, 8.0, 15.0, 11.0)
    ).optimize();
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            box(8.0, 2.0, 3.0, 9.0, 14.0, 13.0),
            box(8.0, 1.0, 4.0, 9.0, 2.0, 11.0),
            box(8.0, 14.0, 5.0, 9.0, 15.0, 12.0)
    ).optimize();
    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, NORTH_SHAPE,
            Direction.SOUTH, SOUTH_SHAPE,
            Direction.EAST, EAST_SHAPE,
            Direction.WEST, WEST_SHAPE);

    public FleetingMemorialBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return SHAPES.get(direction);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FleetingMemorialBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        final var isFacingX = state.getValue(FACING).getAxis() == Direction.Axis.X;
        for (int i = 0; i < 4; i++) {
            final var wideOffset = (random.nextDouble() - 0.5) * 0.6;
            final var narrowOffset = (random.nextDouble() - 0.5) * 0.15;
            level.addParticle(ParticleTypes.ASH,
                    pos.getX() + 0.5 + (isFacingX ? narrowOffset : wideOffset),
                    pos.getY() + 0.9 + random.nextDouble() * 0.1,
                    pos.getZ() + 0.5 + (isFacingX ? wideOffset : narrowOffset),
                    0,
                    0,
                    0);
        }
    }

    @Override
    protected boolean shouldOpenMenuWhenPlaced() {
        return false;
    }

    @Nullable
    @Override
    protected InteractionResult handleEditActions(Level world, Player player, WaystoneBlockEntityBase blockEntity, Waystone waystone) {
        return null;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
