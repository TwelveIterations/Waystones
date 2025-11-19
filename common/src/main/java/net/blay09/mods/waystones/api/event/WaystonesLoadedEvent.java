package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.store.SavedDataWaystonesStore;

// TODO
@Deprecated
public class WaystonesLoadedEvent {
    private final SavedDataWaystonesStore waystoneManager;

    public WaystonesLoadedEvent(SavedDataWaystonesStore waystoneManager) {
        this.waystoneManager = waystoneManager;
    }

    public SavedDataWaystonesStore getWaystoneManager() {
        return waystoneManager;
    }
}
