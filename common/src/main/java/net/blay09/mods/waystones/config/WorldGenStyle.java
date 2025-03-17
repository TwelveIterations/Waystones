package net.blay09.mods.waystones.config;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WorldGenStyle implements StringRepresentable {
    DEFAULT,
    MOSSY,
    SANDY,
    BLACKSTONE,
    DEEPSLATE,
    END_STONE,
    BIOME;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
