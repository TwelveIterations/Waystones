package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record WaystoneActivatedEvent(Player player, Waystone waystone) {
    public static final BidirectionalEventMapper<Consumer<WaystoneActivatedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystoneActivatedEvent.class);
}
