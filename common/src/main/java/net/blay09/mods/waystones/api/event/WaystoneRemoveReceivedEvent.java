package net.blay09.mods.waystones.api.event;

import net.minecraft.resources.Identifier;

import java.util.UUID;

/**
 * This event is fired on the client side when the client has been notified of a waystone being removed.
 * // TODO
 */
@Deprecated
public class WaystoneRemoveReceivedEvent {
    private final Identifier waystoneType;
    private final UUID waystoneId;
    private final boolean wasDestroyed;

    public WaystoneRemoveReceivedEvent(Identifier waystoneType, UUID waystoneId, boolean wasDestroyed) {
        this.waystoneType = waystoneType;
        this.waystoneId = waystoneId;
        this.wasDestroyed = wasDestroyed;
    }

    public Identifier getWaystoneType() {
        return waystoneType;
    }

    public UUID getWaystoneId() {
        return waystoneId;
    }

    /**
     * @return true if the waystone was destroyed, i.e. it is not just being moved with silk touch
     */
    public boolean wasDestroyed() {
        return wasDestroyed;
    }
}
