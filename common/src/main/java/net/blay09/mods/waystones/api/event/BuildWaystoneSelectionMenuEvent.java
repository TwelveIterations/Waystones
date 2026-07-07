package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Fired on the server side when the list of waystones for a waystone selection menu is built.
 * <p>
 * The waystone list is mutable. Mods may add, remove or reorder entries before the menu is sent to the client.
 */
public class BuildWaystoneSelectionMenuEvent {
    public static final BidirectionalEventMapper<Consumer<BuildWaystoneSelectionMenuEvent>> EVENT = Balmstrap.createBoundCustomEvent(BuildWaystoneSelectionMenuEvent.class);

    private final Player player;
    private final @Nullable Waystone sourceWaystone;
    private final List<UserDecoratedWaystone> waystones;
    private final Set<Identifier> flags;
    private final @Nullable Identifier targetKind;
    private final ItemStack warpItem;

    public BuildWaystoneSelectionMenuEvent(Player player, @Nullable Waystone sourceWaystone, List<UserDecoratedWaystone> waystones, Set<Identifier> flags, @Nullable Identifier targetKind, ItemStack warpItem) {
        this.player = player;
        this.sourceWaystone = sourceWaystone;
        this.waystones = waystones;
        this.flags = flags;
        this.targetKind = targetKind;
        this.warpItem = warpItem;
    }

    public Player getPlayer() {
        return player;
    }

    public @Nullable Waystone getSourceWaystone() {
        return sourceWaystone;
    }

    public List<UserDecoratedWaystone> getWaystones() {
        return waystones;
    }

    public void addWaystone(UserDecoratedWaystone waystone) {
        waystones.add(Objects.requireNonNull(waystone));
    }

    public Set<Identifier> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    @Nullable
    public Identifier getTargetKind() {
        return targetKind;
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }
}
