package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fired when a player logs in and is used to set up their personal group registry.
 * <p>
 * Add inbuilt groups that should show up in the player's Manage Group UI right away, even if no matching waystone has been activated yet.
 */
public class CollectDefaultWaystoneGroupsEvent {

    public static final BidirectionalEventMapper<Consumer<CollectDefaultWaystoneGroupsEvent>> EVENT = Balmstrap.createBoundCustomEvent(CollectDefaultWaystoneGroupsEvent.class);

    private final Player player;
    private final List<WaystoneGroup> groups;

    public CollectDefaultWaystoneGroupsEvent(Player player, List<WaystoneGroup> groups) {
        this.player = player;
        this.groups = groups;
    }

    public Player getPlayer() {
        return player;
    }

    public List<WaystoneGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public void addGroup(WaystoneGroup group) {
        groups.add(Objects.requireNonNull(group));
    }

    public void addGroups(Collection<WaystoneGroup> groups) {
        groups.forEach(this::addGroup);
    }
}
