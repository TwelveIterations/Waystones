package net.blay09.mods.waystones.api;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WaystoneOrigin implements StringRepresentable {
    UNKNOWN,
    WILDERNESS,
    DUNGEON,
    VILLAGE,
    PLAYER;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
