package net.blay09.mods.waystones.menu;

import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.waystones.api.MutablePersonalizedWaystone;
import net.blay09.mods.waystones.api.PersonalizedWaystone;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.event.BuildWaystoneSelectionMenuEvent;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaystoneSelectionListBuilder {
    private final ServerPlayer player;
    private final List<MutablePersonalizedWaystone> waystones = new ArrayList<>();
    private @Nullable MutablePersonalizedWaystone sourceWaystone;
    private Set<Identifier> flags = Collections.emptySet();
    private @Nullable Identifier targetKind;
    private ItemStack warpItem = ItemStack.EMPTY;
    private @Nullable InteractionHand warpHand;
    private Consumer<WaystoneTeleportContext> postTeleportHandler = _ -> {};
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

    public WaystoneSelectionListBuilder withTargetsForWaystoneType(Identifier waystoneType) {
        return withWaystones(PlayerWaystoneManager.getTargetsForKind(player, waystoneType))
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

    public WaystoneSelectionListBuilder withPersonalizedWaystones(Collection<? extends MutablePersonalizedWaystone> waystones) {
        this.waystones.addAll(waystones);
        return this;
    }

    public WaystoneSelectionListBuilder withSourceWaystone(@Nullable Waystone sourceWaystone) {
        this.sourceWaystone = sourceWaystone != null ? PlayerWaystoneManager.getPlayerDecoratedWaystone(player, sourceWaystone) : null;
        return this;
    }

    public @Nullable MutablePersonalizedWaystone getSourceWaystone() {
        return sourceWaystone;
    }

    public WaystoneSelectionListBuilder withFlags(Set<Identifier> flags) {
        this.flags = flags;
        return this;
    }

    public WaystoneSelectionListBuilder withTargetKind(@Nullable Identifier targetKind) {
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

    public WaystoneSelectionListBuilder sorted(Comparator<? super PersonalizedWaystone> comparator) {
        waystones.sort(comparator);
        return this;
    }

    public WaystoneSelectionListBuilder skipSortingIndexUpdate() {
        updateSortingIndex = false;
        return this;
    }

    public List<MutablePersonalizedWaystone> build() {
        BuildWaystoneSelectionMenuEvent.EVENT.invoker().accept(new BuildWaystoneSelectionMenuEvent(player, sourceWaystone, waystones, flags, targetKind, warpItem));
        if (updateSortingIndex) {
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
        }
        return List.copyOf(waystones);
    }

    public BalmMenuProvider<?> buildMenuProvider(MenuType<WaystoneSelectionMenu> menuType, Component displayName) {
        final var serverPlayer = player;
        final var builtWaystones = build();
        return new BalmMenuProvider<WaystoneSelectionMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                final var warpRequirements = WaystoneSelectionMenu.buildWarpRequirements((ServerPlayer) player, sourceWaystone, builtWaystones, flags, warpItem, warpHand);
                return new WaystoneSelectionMenu(menuType, sourceWaystone, windowId, builtWaystones, warpRequirements, flags, targetKind)
                        .withWarpItem(warpItem)
                        .withHand(warpHand)
                        .setPostTeleportHandler(postTeleportHandler);
            }

            @Override
            public WaystoneSelectionMenu.Data getScreenOpeningData(ServerPlayer serverPlayer) {
                final var warpRequirements = WaystoneSelectionMenu.buildWarpRequirements(serverPlayer, sourceWaystone, builtWaystones, flags, warpItem, warpHand);
                return new WaystoneSelectionMenu.Data(sourceWaystone, builtWaystones, warpRequirements);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneSelectionMenu.Data> getScreenStreamCodec() {
                return WaystoneSelectionMenu.STREAM_CODEC;
            }
        };
    }

}
