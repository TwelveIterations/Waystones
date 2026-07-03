package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaystoneSelectionMenu extends AbstractContainerMenu {

    public record Data(UserDecoratedWaystone fromWaystone, List<UserDecoratedWaystone> waystones) {
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
            UserDecoratedWaystone.STREAM_CODEC,
            Data::fromWaystone,
            UserDecoratedWaystone.LIST_STREAM_CODEC,
            Data::waystones,
            Data::new);

    private final @Nullable Waystone fromWaystone;
    private final Collection<UserDecoratedWaystone> waystones;
    private final Set<ResourceLocation> flags;
    private Consumer<WaystoneTeleportContext> postTeleportHandler = it -> {};
    private ItemStack warpItem = ItemStack.EMPTY;
    private @Nullable InteractionHand warpHand;
    private final @Nullable ResourceLocation targetKind;

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, @Nullable Waystone fromWaystone, int windowId, Collection<UserDecoratedWaystone> waystones, Set<ResourceLocation> flags) {
        this(type, fromWaystone, windowId, waystones, flags, null);
    }

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, @Nullable Waystone fromWaystone, int windowId, Collection<UserDecoratedWaystone> waystones, Set<ResourceLocation> flags, @Nullable ResourceLocation targetKind) {
        super(type, windowId);
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
        this.flags = flags;
        this.targetKind = targetKind;
    }

    public WaystoneSelectionMenu withWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    public WaystoneSelectionMenu withHand(@Nullable InteractionHand warpHand) {
        this.warpHand = warpHand;
        return this;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (fromWaystone != null) {
            return player.distanceToSqr(fromWaystone.getPos().getCenter()) <= 64;
        }

        return true;
    }

    @Nullable
    public Waystone getWaystoneFrom() {
        return fromWaystone;
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }

    public @Nullable InteractionHand getWarpHand() {
        return warpHand;
    }

    public Collection<UserDecoratedWaystone> getWaystones() {
        return waystones;
    }

    public Set<ResourceLocation> getFlags() {
        return flags;
    }

    public @Nullable ResourceLocation getTargetKind() {
        return targetKind;
    }

    public Consumer<WaystoneTeleportContext> getPostTeleportHandler() {
        return postTeleportHandler;
    }

    public WaystoneSelectionMenu setPostTeleportHandler(Consumer<WaystoneTeleportContext> postTeleportHandler) {
        this.postTeleportHandler = postTeleportHandler;
        return this;
    }
}
