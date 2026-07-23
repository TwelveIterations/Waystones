package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Consumer;

/**
 * This event is fired on the client side when the client has received a list of waystones of the player.
 * <p>
 * This event will be fired for waystones, warp plates and sharestones.
 * Note that for WaystoneTypes.WAYSTONE, the list will only contain the waystones that the player has discovered.
 */
public record WaystonesListReceivedEvent(Identifier waystoneType, List<Waystone> waystones) {
    public static final BidirectionalEventMapper<Consumer<WaystonesListReceivedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystonesListReceivedEvent.class);
}
