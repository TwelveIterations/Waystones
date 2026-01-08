package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.core.SharestoneSelectionEntry;
import net.blay09.mods.waystones.core.WaystoneImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSelectionMenu extends AbstractContainerMenu {

    public record Data(Waystone fromWaystone, List<Waystone> waystones, List<SharestoneSelectionEntry> restrictedEntries) {
        public static Data forWaystones(Waystone fromWaystone, Collection<Waystone> waystones) {
            return new Data(fromWaystone, new ArrayList<>(waystones), Collections.emptyList());
        }

        public static Data forRestrictedEntries(Waystone fromWaystone, Collection<SharestoneSelectionEntry> restrictedEntries) {
            return new Data(fromWaystone, Collections.emptyList(), List.copyOf(restrictedEntries));
        }

        public boolean hasRestrictedEntries() {
            return !restrictedEntries.isEmpty();
        }

        public Collection<Waystone> resolveWaystones() {
            if (hasRestrictedEntries()) {
                return restrictedEntries.stream()
                        .map(SharestoneSelectionEntry::toRestrictedWaystone)
                        .collect(Collectors.toList());
            }
            return waystones;
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.of(
            WaystoneSelectionMenu::writeData,
            WaystoneSelectionMenu::readData);

    private final Waystone fromWaystone;
    private final Collection<Waystone> waystones;
    private final Set<ResourceLocation> flags;
    private Consumer<WaystoneTeleportContext> postTeleportHandler = it -> {};
    private ItemStack warpItem = ItemStack.EMPTY;

    public WaystoneSelectionMenu(MenuType<WaystoneSelectionMenu> type, @Nullable Waystone fromWaystone, int windowId, Collection<Waystone> waystones, Set<ResourceLocation> flags) {
        super(type, windowId);
        this.fromWaystone = fromWaystone;
        this.waystones = waystones;
        this.flags = flags;
    }

    public WaystoneSelectionMenu withWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (fromWaystone != null) {
            BlockPos pos = fromWaystone.getPos();
            return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
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

    public Collection<Waystone> getWaystones() {
        return waystones;
    }

    public Set<ResourceLocation> getFlags() {
        return flags;
    }

    public Consumer<WaystoneTeleportContext> getPostTeleportHandler() {
        return postTeleportHandler;
    }

    public WaystoneSelectionMenu setPostTeleportHandler(Consumer<WaystoneTeleportContext> postTeleportHandler) {
        this.postTeleportHandler = postTeleportHandler;
        return this;
    }

    private static void writeData(RegistryFriendlyByteBuf buf, Data data) {
        WaystoneImpl.STREAM_CODEC.encode(buf, data.fromWaystone());
        buf.writeBoolean(data.hasRestrictedEntries());
        if (data.hasRestrictedEntries()) {
            SharestoneSelectionEntry.LIST_STREAM_CODEC.encode(buf, data.restrictedEntries());
        } else {
            WaystoneImpl.LIST_STREAM_CODEC.encode(buf, data.waystones());
        }
    }

    private static Data readData(RegistryFriendlyByteBuf buf) {
        final var fromWaystone = WaystoneImpl.STREAM_CODEC.decode(buf);
        if (buf.readBoolean()) {
            final var entries = SharestoneSelectionEntry.LIST_STREAM_CODEC.decode(buf);
            return Data.forRestrictedEntries(fromWaystone, entries);
        }
        final var waystones = WaystoneImpl.LIST_STREAM_CODEC.decode(buf);
        return Data.forWaystones(fromWaystone, waystones);
    }
}
