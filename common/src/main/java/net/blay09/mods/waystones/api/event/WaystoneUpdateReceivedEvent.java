package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;

/**
 * This event is on the client side when the client has received an update to a waystone.
 * @deprecated TODO
 */
@Deprecated
public class WaystoneUpdateReceivedEvent {
    private final Waystone waystone;

    public WaystoneUpdateReceivedEvent(Waystone waystone) {
        this.waystone = waystone;
    }

    public Waystone getWaystone() {
        return waystone;
    }
}
