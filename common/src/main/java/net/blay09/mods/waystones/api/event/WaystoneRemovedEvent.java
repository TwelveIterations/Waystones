package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;

// TODO
@Deprecated
public class WaystoneRemovedEvent {
    private final Waystone waystone;

    public WaystoneRemovedEvent(Waystone waystone) {
        this.waystone = waystone;
    }

    public Waystone getWaystone() {
        return waystone;
    }
}
