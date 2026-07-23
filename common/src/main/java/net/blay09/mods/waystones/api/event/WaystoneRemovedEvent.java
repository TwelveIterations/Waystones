package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.Waystone;

import java.util.function.Consumer;

public record WaystoneRemovedEvent(Waystone waystone) {
    public static final BidirectionalEventMapper<Consumer<WaystoneRemovedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystoneRemovedEvent.class);
}
