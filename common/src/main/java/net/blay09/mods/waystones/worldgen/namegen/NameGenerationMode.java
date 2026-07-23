package net.blay09.mods.waystones.worldgen.namegen;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum NameGenerationMode implements StringRepresentable {
    PRESET_FIRST,
    RANDOM_ONLY,
    PRESET_ONLY,
    MIXED;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
