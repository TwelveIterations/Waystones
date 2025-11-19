package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;

/**
 * // TODO
 */
@Deprecated
public class WaystoneUpdatedEvent {
    private final Waystone waystone;

    public WaystoneUpdatedEvent(Waystone waystone) {
        this.waystone = waystone;
    }

    public Waystone getWaystone() {
        return waystone;
    }
}
