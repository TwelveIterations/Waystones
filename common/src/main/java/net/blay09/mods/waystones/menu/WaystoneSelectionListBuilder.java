package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.event.BuildWaystoneSelectionMenuEvent;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaystoneSelectionListBuilder {
    private final ServerPlayer player;
    private final List<UserDecoratedWaystone> waystones = new ArrayList<>();
    private @Nullable UserDecoratedWaystone sourceWaystone;
    private Set<ResourceLocation> flags = Collections.emptySet();
    private @Nullable ResourceLocation targetKind;
    private ItemStack warpItem = ItemStack.EMPTY;
    private @Nullable InteractionHand warpHand;
    private Consumer<WaystoneTeleportContext> postTeleportHandler = it -> {};
    private boolean updateSortingIndex = true;

    public WaystoneSelectionListBuilder(ServerPlayer player) {
        this.player = player;
    }

    public WaystoneSelectionListBuilder withActivatedWaystones() {
        return withActivatedWaystones(player);
    }

    public WaystoneSelectionListBuilder withActivatedWaystones(Player targetPlayer) {
        return withWaystones(targetPlayer, PlayerWaystoneManager.getActivatedWaystones(targetPlayer));
    }

    public WaystoneSelectionListBuilder withTargetsForPlayer() {
        return withWaystones(PlayerWaystoneManager.getTargetsForPlayer(player));
    }

    public WaystoneSelectionListBuilder withTargetsForItem(ItemStack itemStack) {
        return withWaystones(PlayerWaystoneManager.getTargetsForItem(player, itemStack))
                .withWarpItem(itemStack);
    }

    public WaystoneSelectionListBuilder withTargetsForWaystone(Waystone waystone) {
        return withSourceWaystone(waystone)
                .withWaystones(PlayerWaystoneManager.getTargetsForWaystone(player, waystone));
    }

    public WaystoneSelectionListBuilder withTargetsForWaystoneType(ResourceLocation waystoneType) {
        return withWaystones(PlayerWaystoneManager.getTargetsForWaystoneType(player, waystoneType))
                .withTargetKind(waystoneType);
    }

    public WaystoneSelectionListBuilder withInventoryButtonTargets() {
        return withWaystones(PlayerWaystoneManager.getTargetsForInventoryButton(player));
    }

    public WaystoneSelectionListBuilder withWaystones(Collection<Waystone> waystones) {
        return withWaystones(player, waystones);
    }

    public WaystoneSelectionListBuilder withWaystones(Player decorationPlayer, Collection<Waystone> waystones) {
        this.waystones.addAll(PlayerWaystoneManager.getPlayerDecoratedWaystones(decorationPlayer, waystones));
        return this;
    }

    public WaystoneSelectionListBuilder withDecoratedWaystones(Collection<UserDecoratedWaystone> waystones) {
        this.waystones.addAll(waystones);
        return this;
    }

    public WaystoneSelectionListBuilder withSourceWaystone(@Nullable Waystone sourceWaystone) {
        this.sourceWaystone = sourceWaystone != null ? PlayerWaystoneManager.getPlayerDecoratedWaystone(player, sourceWaystone) : null;
        return this;
    }

    public @Nullable UserDecoratedWaystone getSourceWaystone() {
        return sourceWaystone;
    }

    public WaystoneSelectionListBuilder withFlags(Set<ResourceLocation> flags) {
        this.flags = flags;
        return this;
    }

    public WaystoneSelectionListBuilder withTargetKind(@Nullable ResourceLocation targetKind) {
        this.targetKind = targetKind;
        return this;
    }

    public WaystoneSelectionListBuilder withWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    public WaystoneSelectionListBuilder withHand(@Nullable InteractionHand warpHand) {
        this.warpHand = warpHand;
        return this;
    }

    public WaystoneSelectionListBuilder withPostTeleportHandler(Consumer<WaystoneTeleportContext> postTeleportHandler) {
        this.postTeleportHandler = postTeleportHandler;
        return this;
    }

    public WaystoneSelectionListBuilder sorted(Comparator<? super UserDecoratedWaystone> comparator) {
        waystones.sort(comparator);
        return this;
    }

    public WaystoneSelectionListBuilder skipSortingIndexUpdate() {
        updateSortingIndex = false;
        return this;
    }

    public List<UserDecoratedWaystone> build() {
        Balm.getEvents().fireEvent(new BuildWaystoneSelectionMenuEvent(player, sourceWaystone, waystones, flags, targetKind, warpItem));
        if (updateSortingIndex) {
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
        }
        return waystones;
    }

    public BalmMenuProvider<?> buildMenuProvider(MenuType<WaystoneSelectionMenu> menuType, Component displayName) {
        final var builtWaystones = build();
        return new BalmMenuProvider<WaystoneSelectionMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                return createWaystoneSelectionMenu(menuType, windowId, builtWaystones);
            }

            @Override
            public WaystoneSelectionMenu.Data getScreenOpeningData(ServerPlayer serverPlayer) {
                return new WaystoneSelectionMenu.Data(sourceWaystone, builtWaystones, warpItem, targetKind);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getScreenStreamCodec() {
                return WaystoneSelectionMenu.STREAM_CODEC;
            }
        };
    }

    private WaystoneSelectionMenu createWaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> menuType, int windowId, List<UserDecoratedWaystone> builtWaystones) {
        return new WaystoneSelectionMenu(menuType, sourceWaystone, windowId, builtWaystones, flags, targetKind)
                .withWarpItem(warpItem)
                .withHand(warpHand)
                .setPostTeleportHandler(postTeleportHandler);
    }
}
