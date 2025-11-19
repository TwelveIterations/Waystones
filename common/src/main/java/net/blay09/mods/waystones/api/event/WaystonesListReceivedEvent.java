package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * This event is fired on the client side when the client has received a list of waystones of the player.
 * <p>
 * This event will be fired for waystones, warp plates and sharestones.
 * Note that for WaystoneTypes.WAYSTONE, the list will only contain the waystones that the player has discovered.
 */
@Deprecated // TODO
public class WaystonesListReceivedEvent {

    private final Identifier waystoneType;
    private final List<Waystone> waystones;

    public WaystonesListReceivedEvent(Identifier waystoneType, List<Waystone> waystones) {
        this.waystoneType = waystoneType;
        this.waystones = waystones;
    }

    public Identifier getWaystoneType() {
        return waystoneType;
    }

    public List<Waystone> getWaystones() {
        return waystones;
    }
}
