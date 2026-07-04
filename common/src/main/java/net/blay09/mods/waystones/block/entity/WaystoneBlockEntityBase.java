package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.balm.world.level.block.entity.OnLoadHandler;
import net.blay09.mods.waystones.api.MutableWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.component.WaystoneReferenceComponent;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.menu.WaystoneModifierMenu;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class WaystoneBlockEntityBase extends BlockEntity implements OnLoadHandler, BalmContainerProvider {

    protected final DefaultContainer container = new DefaultContainer(5) {
        @Override
        public int getMaxStackSize(ItemStack itemStack) {
            if (itemStack.is(ModItems.dormantShard)) {
                return 1;
            }

            return super.getMaxStackSize(itemStack);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            if (itemStack.is(ModItems.dormantShard)) {
                return slot == 0;
            }

            return super.canPlaceItem(slot, itemStack);
        }

        @Override
        public void setChanged() {
            onInventoryChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(WaystoneBlockEntityBase.this, player);
        }
    };

    protected void onInventoryChanged() {
    }

    private Waystone waystone = InvalidWaystone.INSTANCE;
    private @Nullable UUID waystoneUid;
    private boolean shouldNotInitialize;
    private boolean silkTouched;

    public WaystoneBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, container.getItems());

        output.storeNullable("UUID", UUIDUtil.CODEC, getEffectiveWaystoneUid());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, container.getItems());

        input.read("UUID", UUIDUtil.CODEC).ifPresent(uuid -> waystoneUid = uuid);

        input.read("Waystone", WaystoneImpl.CODEC.codec()).ifPresent(loadedWaystone -> waystone = loadedWaystone);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        final var waystoneUidFromComponent = Optional.ofNullable(input.get(ModComponents.waystoneIdentity.value()))
                .map(WaystoneReferenceComponent::waystoneId)
                .orElseGet(() -> input.get(ModComponents.waystone.value()));
        if (waystoneUidFromComponent != null) {
            waystoneUid = waystoneUidFromComponent;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(ModComponents.waystoneIdentity.value(), new WaystoneReferenceComponent(getEffectiveWaystoneUid(), waystone.getName()));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, output -> output.store("Waystone", WaystoneImpl.CODEC.codec(), getWaystone()));
    }

    @Override
    public void onLoad() {
        final var backingWaystone = loadBackingWaystone();
        if (backingWaystone instanceof WaystoneImpl && level != null) {
            ((WaystoneImpl) backingWaystone).setDimension(level.dimension());
            ((WaystoneImpl) backingWaystone).setPos(worldPosition);
        }
        BalmBlockEntityUtils.sync(this);
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 2,
                worldPosition.getZ() + 1);
    }

    private Waystone loadBackingWaystone() {
        if (!waystone.isValid() && level instanceof ServerLevel serverLevel && !shouldNotInitialize) {
            if (waystoneUid != null) {
                waystone = new WaystoneProxy(serverLevel.getServer(), waystoneUid);
            }

            if (!waystone.isValid()) {
                BlockState state = getBlockState();
                if (state.getBlock() instanceof WaystoneBlockBase) {
                    DoubleBlockHalf half = state.hasProperty(WaystoneBlockBase.HALF) ? state.getValue(WaystoneBlockBase.HALF) : DoubleBlockHalf.LOWER;
                    WaystoneOrigin origin = state.hasProperty(WaystoneBlockBase.ORIGIN) ? state.getValue(WaystoneBlockBase.ORIGIN) : WaystoneOrigin.UNKNOWN;
                    if (half == DoubleBlockHalf.LOWER) {
                        initializeWaystone((ServerLevelAccessor) Objects.requireNonNull(level), null, origin);
                    } else if (half == DoubleBlockHalf.UPPER) {
                        BlockEntity blockEntity = level.getBlockEntity(worldPosition.below());
                        if (blockEntity instanceof WaystoneBlockEntityBase) {
                            initializeFromBase(((WaystoneBlockEntityBase) blockEntity));
                        }
                    }
                }
            }

            if (waystone.isValid()) {
                waystoneUid = waystone.getWaystoneUid();
                if (waystone instanceof MutableWaystone mutableWaystone) {
                    mutableWaystone.setDimension(level.dimension());
                    mutableWaystone.setPos(worldPosition);
                    mutableWaystone.setTransient(false);
                }
                SavedDataWaystonesStore.get(level.getServer()).updateWaystone(waystone);
                setChanged();
            }
        }

        return waystone;
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public Waystone getOrLoadWaystone() {
        return waystone.isValid() ? waystone : loadBackingWaystone();
    }

    protected abstract Identifier getWaystoneKind();

    public void initializeWaystone(ServerLevelAccessor level, @Nullable LivingEntity player, WaystoneOrigin origin) {
        if (!this.waystone.isValid()) {
            WaystoneImpl waystone = new WaystoneImpl(getWaystoneKind(),
                    UUID.randomUUID(),
                    level.getLevel().dimension(),
                    worldPosition,
                    origin,
                    player != null ? player.getUUID() : null,
                    player instanceof Player owner ? owner.getGameProfile().name() : null);
            SavedDataWaystonesStore.get(level.getLevel().getServer()).addWaystone(waystone);
            WaystoneInitializedEvent.EVENT.invoker().accept(new WaystoneInitializedEvent(waystone));
            this.waystone = waystone;
            setChanged();
            BalmBlockEntityUtils.sync(this);
        }
    }

    public void initializeFromExisting(ServerLevelAccessor level, WaystoneImpl existingWaystone, ItemStack itemStack) {
        waystone = existingWaystone;
        existingWaystone.setDimension(level.getLevel().dimension());
        existingWaystone.setPos(worldPosition);
        existingWaystone.setTransient(false);
        SavedDataWaystonesStore.get(level.getLevel().getServer()).updateWaystone(waystone);
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    public void initializeFromBase(WaystoneBlockEntityBase tileEntity) {
        waystone = tileEntity.getWaystone();
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    @SuppressWarnings("unused") // for WaystonesSable and others
    public void detachWaystone() {
        if (waystone instanceof MutableWaystone mutableWaystone) {
            mutableWaystone.setTransient(true);
        }
        waystone = InvalidWaystone.INSTANCE;
        waystoneUid = null;

        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    public void uninitializeWaystone() {
        if (level instanceof ServerLevel serverLevel) {
            if (waystone.isValid()) {
                final var server = serverLevel.getServer();
                SavedDataWaystonesStore.get(server).removeWaystone(waystone);
                PlayerWaystoneManager.removeKnownWaystone(server, waystone);
                WaystoneSyncManager.sendWaystoneRemovalToAll(server, waystone, true);
            }

            waystone = InvalidWaystone.INSTANCE;
            shouldNotInitialize = true;

            final var blockState = getBlockState();
            if (blockState.hasProperty(WaystoneBlock.HALF)) {
                DoubleBlockHalf half = blockState.getValue(WaystoneBlock.HALF);
                BlockPos otherPos = half == DoubleBlockHalf.UPPER ? worldPosition.below() : worldPosition.above();
                BlockEntity blockEntity = Objects.requireNonNull(level).getBlockEntity(otherPos);
                if (blockEntity instanceof WaystoneBlockEntityBase waystoneTile) {
                    waystoneTile.waystone = InvalidWaystone.INSTANCE;
                    waystoneTile.shouldNotInitialize = true;
                }
            }

            setChanged();
            BalmBlockEntityUtils.sync(this);
        }
    }

    public void setSilkTouched(boolean silkTouched) {
        this.silkTouched = silkTouched;
    }

    public boolean canSilkTouch() {
        return false;
    }

    public boolean isSilkTouched() {
        return silkTouched;
    }

    public Optional<MenuProvider> getSelectionMenuProvider(ServerPlayer player) {
        return Optional.empty();
    }

    public abstract Component getName();

    public Optional<MenuProvider> getSettingsMenuProvider(ServerPlayer player) {
        final var waystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, getWaystone());
        final var error = WaystonePermissionManager.mayEditWaystone(player, getWaystone());
        final var visibilityOptions = WaystoneVisibilities.getVisibilityOptions(player, waystone);
        return Optional.of(new BalmMenuProvider<WaystoneEditMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.waystone_settings", getName());
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                return new WaystoneEditMenu(i,
                        waystone,
                        getModifierCount(),
                        error.orElse(null),
                        visibilityOptions,
                        getContainer());
            }

            @Override
            public WaystoneEditMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new WaystoneEditMenu.Data(worldPosition,
                        waystone,
                        getModifierCount(),
                        error,
                        visibilityOptions);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> getScreenStreamCodec() {
                return WaystoneEditMenu.STREAM_CODEC;
            }
        });
    }

    public Optional<MenuProvider> getModifierMenuProvider(ServerPlayer player) {
        final var waystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, getWaystone());
        return Optional.of(new BalmMenuProvider<UserDecoratedWaystone>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.waystones.waystone_modifiers");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                return new WaystoneModifierMenu(i, playerInventory, waystone, getContainer());
            }

            @Override
            public UserDecoratedWaystone getScreenOpeningData(ServerPlayer serverPlayer) {
                return waystone;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, UserDecoratedWaystone> getScreenStreamCodec() {
                return UserDecoratedWaystone.STREAM_CODEC;
            }
        });
    }

    public Collection<? extends Waystone> getAuxiliaryTargets() {
        final var result = new ArrayList<Waystone>();
        final var baseContainer = getContainer();
        if (baseContainer != null) {
            for (int i = 0; i < baseContainer.getContainerSize(); i++) {
                final var item = baseContainer.getItem(i);
                WaystonesAPI.getBoundWaystone(null, item).ifPresent(result::add);
            }
        }
        return result;
    }

    @Override
    public Container getContainer() {
        // If we're the upper half, return the lower half's container if it exists
        final var state = getBlockState();
        if (state.getValueOrElse(WaystoneBlockBase.HALF, DoubleBlockHalf.LOWER) == DoubleBlockHalf.UPPER) {
            final var baseBlockEntity = level.getBlockEntity(worldPosition.below());
            if (baseBlockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
                return waystoneBlockEntity.container;
            }
        }

        return container;
    }

    private int getModifierCount() {
        var modifiers = 0;
        final var baseContainer = getContainer();
        if (baseContainer != null) {
            for (int i = 0; i < baseContainer.getContainerSize(); i++) {
                ItemStack itemStack = baseContainer.getItem(i);
                if (itemStack.is(ModItemTags.WARP_MODIFIERS)) {
                    modifiers++;
                }
            }
        }
        return modifiers;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        dropItems(level, pos);

        if (level instanceof ServerLevel serverLevel) {
            final var waystone = getWaystone();
            if (waystone.isValid()) {
                final var wasNotSilkTouched = !canSilkTouch() || !isSilkTouched();
                WaystoneSyncManager.sendWaystoneRemovalToAll(serverLevel.getServer(), waystone, wasNotSilkTouched);
                if (wasNotSilkTouched) {
                    SavedDataWaystonesStore.get(serverLevel.getServer()).removeWaystone(waystone);
                    PlayerWaystoneManager.removeKnownWaystone(serverLevel.getServer(), waystone);
                } else if (waystone instanceof MutableWaystone mutableWaystone) {
                    mutableWaystone.setTransient(true);
                    SavedDataWaystonesStore.get(serverLevel.getServer()).updateWaystone(waystone);
                }
            }
        }
    }

    protected UUID getEffectiveWaystoneUid() {
        return waystone.isValid() ? waystone.getWaystoneUid() : waystoneUid;
    }


}
