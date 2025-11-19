package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.world.entity.player.Player;

/**
 * TODO
 */
@Deprecated
public class WaystoneActivatedEvent {
    private final Player player;
    private final Waystone waystone;

    public WaystoneActivatedEvent(Player player, Waystone waystone) {
        this.player = player;
        this.waystone = waystone;
    }

    public Player getPlayer() {
        return player;
    }

    public Waystone getWaystone() {
        return waystone;
    }
}
