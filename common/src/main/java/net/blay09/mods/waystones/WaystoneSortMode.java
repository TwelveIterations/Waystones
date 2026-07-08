package net.blay09.mods.waystones;

import net.minecraft.network.chat.Component;

public enum WaystoneSortMode {
    MANUAL("manual"),
    NAME("name"),
    DISTANCE("distance");

    private final String serializedName;

    WaystoneSortMode(String serializedName) {
        this.serializedName = serializedName;
    }

    public WaystoneSortMode next() {
        final var modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }

    public Component label() {
        return Component.translatable("gui.waystones.waystone_selection.sort." + serializedName);
    }

    public String getSerializedName() {
        return serializedName;
    }

    public static WaystoneSortMode byName(String name) {
        for (final var mode : values()) {
            if (mode.serializedName.equals(name)) {
                return mode;
            }
        }

        return MANUAL;
    }
}
