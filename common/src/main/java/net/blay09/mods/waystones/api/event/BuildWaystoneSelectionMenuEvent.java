package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.MutablePersonalizedWaystone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Fired on the server side when the list of waystones for a waystone selection menu is built.
 * <p>
 * The waystone list is mutable. Mods may add, remove or reorder entries before the menu is sent to the client.
 */
public class BuildWaystoneSelectionMenuEvent extends BalmEvent {
    private final Player player;
    private final @Nullable MutablePersonalizedWaystone sourceWaystone;
    private final List<MutablePersonalizedWaystone> waystones;
    private final Set<ResourceLocation> flags;
    private final @Nullable ResourceLocation targetKind;
    private final ItemStack warpItem;

    public BuildWaystoneSelectionMenuEvent(Player player, @Nullable MutablePersonalizedWaystone sourceWaystone, List<MutablePersonalizedWaystone> waystones, Set<ResourceLocation> flags, @Nullable ResourceLocation targetKind, ItemStack warpItem) {
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

    public @Nullable MutablePersonalizedWaystone getSourceWaystone() {
        return sourceWaystone;
    }

    public List<MutablePersonalizedWaystone> getWaystones() {
        return waystones;
    }

    public void addWaystone(MutablePersonalizedWaystone waystone) {
        waystones.add(Objects.requireNonNull(waystone));
    }

    public Set<ResourceLocation> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    @Nullable
    public ResourceLocation getTargetKind() {
        return targetKind;
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }
}
