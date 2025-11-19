package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;

import java.util.function.Consumer;

/**
 * This event is on the client side when the client has received an update to a waystone.
 */
public record WaystoneUpdateReceivedEvent(Waystone waystone) {
    public static final BidirectionalEventMapper<Consumer<WaystoneUpdateReceivedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystoneUpdateReceivedEvent.class);
}
