package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.minecraft.world.entity.player.Player;

public class WaystoneActivatedEvent extends BalmEvent {
    private final Player player;
    private final IWaystone waystone;

    public WaystoneActivatedEvent(Player player, IWaystone waystone) {
        this.player = player;
        this.waystone = waystone;
    }

    public Player getPlayer() {
        return player;
    }

    public IWaystone getWaystone() {
        return waystone;
    }
}
