package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class WarpPlateBlock extends WaystoneBlockBase {

    public enum WarpPlateStatus implements StringRepresentable {
        IDLE,
        ACTIVE,
        INVALID;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private static final Style GALACTIC_STYLE = Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt"));

    private static final VoxelShape SHAPE = Shapes.or(
            box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            box(3.0, 1.0, 3.0, 13.0, 2.0, 13.0)
    ).optimize();

    @Deprecated
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final EnumProperty<WarpPlateStatus> STATUS = EnumProperty.create("status", WarpPlateStatus.class);

    public WarpPlateBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(ACTIVE, false)
                .setValue(STATUS, WarpPlateStatus.IDLE));
    }

    @Override
    protected boolean canSilkTouch() {
        return true;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpPlateBlockEntity && !player.getAbilities().instabuild) {
            boolean isSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
            WarpPlateBlockEntity warpPlate = (WarpPlateBlockEntity) blockEntity;
            if (warpPlate.isCompletedFirstAttunement()) {
                for (int i = 0; i < warpPlate.getContainerSize(); i++) {
                    ItemStack itemStack = warpPlate.getItem(i);

                    // If not silk touching, don't bother dropping shards attuned to this waystone, since the waystone is gonna die anyways
                    if (!isSilkTouch && itemStack.getItem() == ModItems.attunedShard) {
                        IWaystone waystoneAttunedTo = ((IAttunementItem) ModItems.attunedShard).getWaystoneAttunedTo(world.getServer(), itemStack);
                        if (waystoneAttunedTo != null && waystoneAttunedTo.getWaystoneUid().equals(warpPlate.getWaystone().getWaystoneUid())) {
                            continue;
                        }
                    }

                    popResource(world, pos, itemStack);
                }
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
        builder.add(STATUS);
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
        if (entity.getX() >= pos.getX() && entity.getX() < pos.getX() + 1
                && entity.getY() >= pos.getY() && entity.getY() < pos.getY() + 1
                && entity.getZ() >= pos.getZ() && entity.getZ() < pos.getZ() + 1
                && !world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof WarpPlateBlockEntity) {
                ((WarpPlateBlockEntity) tileEntity).onEntityCollision(entity);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(STATUS) == WarpPlateStatus.ACTIVE) {
            for (int i = 0; i < 50; i++) {
                world.addParticle(ParticleTypes.CRIMSON_SPORE,
                        pos.getX() + Math.random(),
                        pos.getY() + Math.random() * 2,
                        pos.getZ() + Math.random(),
                        0f,
                        0f,
                        0f);
                world.addParticle(ParticleTypes.PORTAL,
                        pos.getX() + Math.random(),
                        pos.getY() + Math.random() * 2,
                        pos.getZ() + Math.random(),
                        0f,
                        0f,
                        0f);
            }
        } else if (state.getValue(STATUS) == WarpPlateStatus.INVALID) {
            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.SMOKE, pos.getX() + Math.random(), pos.getY(), pos.getZ() + Math.random(), 0f, 0.01f, 0f);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpPlateBlockEntity(pos, state);
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
    protected void addWaystoneNameToTooltip(List<Component> tooltip, WaystoneProxy waystone) {
        tooltip.add(getGalacticName(waystone));
    }

    public static ChatFormatting getColorForName(String name) {
        int colorIndex = Math.abs(name.hashCode()) % 15;
        ChatFormatting textFormatting = ChatFormatting.getById(colorIndex);
        if (textFormatting == ChatFormatting.GRAY) {
            return ChatFormatting.LIGHT_PURPLE;
        } else if (textFormatting == ChatFormatting.DARK_GRAY) {
            return ChatFormatting.DARK_PURPLE;
        } else if (textFormatting == ChatFormatting.BLACK) {
            return ChatFormatting.GOLD;
        }
        return textFormatting != null ? textFormatting : ChatFormatting.GRAY;
    }

    public static Component getGalacticName(IWaystone waystone) {
        String name = StringUtils.substringBeforeLast(waystone.getName(), " ");
        var galacticName = Component.literal(name);
        galacticName.withStyle(WarpPlateBlock.getColorForName(name));
        galacticName.withStyle(GALACTIC_STYLE);
        return galacticName;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? null : createTickerHelper(type,
                ModBlockEntities.warpPlate.get(),
                (level, pos, state2, blockEntity) -> blockEntity.serverTick());
    }
}
