package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;

/**
 * @deprecated TODO
 */
@Deprecated
public class WaystoneInitializedEvent {
    private final Waystone waystone;

    public WaystoneInitializedEvent(Waystone waystone) {
        this.waystone = waystone;
    }

    public Waystone getWaystone() {
        return waystone;
    }
}
