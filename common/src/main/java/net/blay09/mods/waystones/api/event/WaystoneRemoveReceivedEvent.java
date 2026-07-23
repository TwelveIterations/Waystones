package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.minecraft.resources.Identifier;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * This event is fired on the client side when the client has been notified of a waystone being removed.
 */
public record WaystoneRemoveReceivedEvent(Identifier waystoneType, UUID waystoneId, boolean wasDestroyed) {
    public static final BidirectionalEventMapper<Consumer<WaystoneRemoveReceivedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystoneRemoveReceivedEvent.class);
}
