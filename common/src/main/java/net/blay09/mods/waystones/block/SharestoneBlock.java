package net.blay09.mods.waystones.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SharestoneBlock extends WaystoneBlockBase {

    public static final MapCodec<SharestoneBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(SharestoneType.CODEC.fieldOf("color")
                    .forGetter(SharestoneBlock::getType), propertiesCodec())
            .apply(instance, SharestoneBlock::new));

    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            box(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
            box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            box(3.0, 9.0, 3.0, 13.0, 16.0, 13.0)
    ).optimize();

    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0),
            box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            box(1.0, 9.0, 1.0, 15.0, 13.0, 15.0),
            box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0)
    ).optimize();

    private final SharestoneType type;

    public SharestoneBlock(SharestoneType type, Properties properties) {
        super(properties);
        this.type = type;
        registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SharestoneBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    protected InteractionResult handleActivation(Level level, BlockPos pos, BlockState state, Player player, WaystoneBlockEntityBase blockEntity, Waystone waystone) {
        if (!level.isClientSide()) {
            blockEntity.getSelectionMenuProvider().ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    public SharestoneType getType() {
        return type;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
