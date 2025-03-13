package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.core.SavedDataWaystonesStore;

public class WaystonesLoadedEvent extends BalmEvent {
    private final SavedDataWaystonesStore waystoneManager;

    public WaystonesLoadedEvent(SavedDataWaystonesStore waystoneManager) {
        this.waystoneManager = waystoneManager;
    }

    public SavedDataWaystonesStore getWaystoneManager() {
        return waystoneManager;
    }
}
