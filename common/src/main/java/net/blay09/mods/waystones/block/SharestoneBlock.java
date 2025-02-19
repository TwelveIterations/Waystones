package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.SharestoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SharestoneBlock extends WaystoneBlockBase {

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

    @Nullable
    private final DyeColor color;

    public SharestoneBlock(Properties properties, @Nullable DyeColor color) {
        super(properties);
        this.color = color;
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
    protected InteractionResult handleActivation(Level world, BlockPos pos, Player player, WaystoneBlockEntityBase tileEntity, IWaystone waystone) {
        if (!world.isClientSide) {
            Balm.getNetworking().openGui(player, tileEntity.getMenuProvider());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag) {
        var component = Component.translatable(color != null ? "tooltip.waystones." + color.getSerializedName() + "_sharestone" : "tooltip.waystones.sharestone");
        component.withStyle(ChatFormatting.GRAY);
        list.add(component);

        super.appendHoverText(stack, world, list, flag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }

}
