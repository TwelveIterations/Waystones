package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;

import java.util.List;

/**
 * This event is fired on the client side when the client has received the known waystones of the player.
 */
public class KnownWaystonesEvent extends BalmEvent {

    private final List<IWaystone> waystones;

    public KnownWaystonesEvent(List<IWaystone> waystones) {
        this.waystones = waystones;
    }

    public List<IWaystone> getWaystones() {
        return waystones;
    }
}
