package net.blay09.mods.waystones.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.entity.PortstoneBlockEntity;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class PortstoneBlock extends WaystoneBlockBase {

    public static final MapCodec<PortstoneBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(SharestoneType.CODEC.optionalFieldOf("type")
                    .forGetter(PortstoneBlock::getType), propertiesCodec())
            .apply(instance, PortstoneBlock::new));

    private static final VoxelShape[] LOWER_SHAPES = new VoxelShape[]{
            // South
            Shapes.or(
                    box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
                    box(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
                    box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
                    box(3.0, 9.0, 3.0, 13.0, 16.0, 7.0),
                    box(4.0, 9.0, 7.0, 12.0, 16.0, 10.0),
                    box(4.0, 9.0, 10.0, 12.0, 12.0, 12.0)
            ).optimize(),
            // West
            Shapes.or(
                    box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
                    box(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
                    box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
                    box(9.0, 9.0, 3.0, 13.0, 16.0, 13.0),
                    box(6.0, 9.0, 4.0, 9.0, 16.0, 12.0),
                    box(4.0, 9.0, 4.0, 6.0, 12.0, 12.0)
            ).optimize(),
            // North
            Shapes.or(
                    box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
                    box(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
                    box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
                    box(3.0, 9.0, 9.0, 13.0, 16.0, 13.0),
                    box(4.0, 9.0, 6.0, 12.0, 16.0, 9.0),
                    box(4.0, 9.0, 4.0, 12.0, 12.0, 6.0)
            ).optimize(),
            // East
            Shapes.or(
                    box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
                    box(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
                    box(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
                    box(3.0, 9.0, 3.0, 7.0, 16.0, 13.0),
                    box(7.0, 9.0, 4.0, 10.0, 16.0, 12.0),
                    box(10.0, 9.0, 4.0, 12.0, 12.0, 12.0)
            ).optimize()
    };

    private static final VoxelShape[] UPPER_SHAPES = new VoxelShape[]{
            // South
            Shapes.or(
                    box(3.0, 0.0, 3.0, 13.0, 7.0, 7.0),
                    box(4.0, 0.0, 7.0, 12.0, 2.0, 9.0)
            ).optimize(),
            // West
            Shapes.or(
                    box(9.0, 0.0, 3.0, 13.0, 7.0, 13.0),
                    box(7.0, 0.0, 4.0, 9.0, 2.0, 12.0)
            ).optimize(),
            // North
            Shapes.or(
                    box(3.0, 0.0, 9.0, 13.0, 7.0, 13.0),
                    box(4.0, 0.0, 7.0, 12.0, 2.0, 9.0)
            ).optimize(),
            // East
            Shapes.or(
                    box(3.0, 0.0, 3.0, 7.0, 7.0, 13.0),
                    box(7.0, 0.0, 4.0, 9.0, 2.0, 12.0)
            ).optimize()
    };

    @Nullable
    private final SharestoneType type;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PortstoneBlock(Optional<SharestoneType> type, Properties properties) {
        this(type.orElse(null), properties);
    }

    public PortstoneBlock(@Nullable SharestoneType type, Properties properties) {
        super(properties);
        this.type = type;
        registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, false));
    }

    public Optional<SharestoneType> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPES[direction.get2DDataValue()] : LOWER_SHAPES[direction.get2DDataValue()];
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortstoneBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (!world.isClientSide()) {
            final var targetWaystoneType = getTargetWaystoneType();
            final var waystones = new ArrayList<>(PlayerWaystoneManager.getTargetsForWaystoneType(player, targetWaystoneType));
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            Balm.networking().openMenu(player, new BalmMenuProvider<ModMenus.WaystoneListMenuData>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones." + type.getSerializedName() + "_portstone");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return new WaystoneSelectionMenu(ModMenus.portstoneSelection.value(), null, windowId, waystones, Collections.emptyMap(), Set.of(TeleportFlags.PORTSTONE));
                }

                @Override
                public ModMenus.WaystoneListMenuData getScreenOpeningData(ServerPlayer serverPlayer) {
                    final var warpRequirements = WaystoneSelectionMenu.buildWarpRequirements(serverPlayer, null, waystones, Set.of(TeleportFlags.PORTSTONE));
                    return new ModMenus.WaystoneListMenuData(waystones, warpRequirements);
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, ModMenus.WaystoneListMenuData> getScreenStreamCodec() {
                    return ModMenus.WaystoneListMenuData.STREAM_CODEC;
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    private Identifier getTargetWaystoneType() {
        return WaystoneTypes.getSharestone(type).orElse(WaystoneTypes.WAYSTONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
