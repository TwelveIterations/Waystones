package net.blay09.mods.waystones.api;

public enum DefaultWaystoneVisibility {
    ACTIVATION(WaystoneVisibility.ACTIVATION),
    GLOBAL(WaystoneVisibility.GLOBAL),
    TEAM(WaystoneVisibility.TEAM);

    private final WaystoneVisibility waystoneVisibility;

    DefaultWaystoneVisibility(WaystoneVisibility waystoneVisibility) {
        this.waystoneVisibility = waystoneVisibility;
    }

    public WaystoneVisibility asWaystoneVisibility() {
        return waystoneVisibility;
    }
}
