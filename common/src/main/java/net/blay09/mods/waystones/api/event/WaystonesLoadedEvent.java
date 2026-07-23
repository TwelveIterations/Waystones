package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;

import java.util.function.Consumer;

public record WaystonesLoadedEvent(SavedDataWaystonesStore waystoneManager) {
    public static final BidirectionalEventMapper<Consumer<WaystonesLoadedEvent>> EVENT = Balmstrap.createBoundCustomEvent(WaystonesLoadedEvent.class);
}
