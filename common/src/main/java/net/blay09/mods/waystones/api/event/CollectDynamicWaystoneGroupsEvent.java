package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fired for each waystone sent in the player's waystone selection menu.
 * <p>
 * Use this to attach dynamic inbuilt groups to this waystone.
 */
public class CollectDynamicWaystoneGroupsEvent {

    public static final BidirectionalEventMapper<Consumer<CollectDynamicWaystoneGroupsEvent>> EVENT = Balmstrap.createBoundCustomEvent(CollectDynamicWaystoneGroupsEvent.class);

    private final Waystone waystone;
    private final List<WaystoneGroup> groups;

    public CollectDynamicWaystoneGroupsEvent(Waystone waystone, List<WaystoneGroup> groups) {
        this.waystone = waystone;
        this.groups = groups;
    }

    public Waystone getWaystone() {
        return waystone;
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
