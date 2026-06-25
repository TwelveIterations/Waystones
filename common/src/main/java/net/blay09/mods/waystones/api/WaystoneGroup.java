package net.blay09.mods.waystones.api;

import net.minecraft.resources.ResourceLocation;

public interface WaystoneGroup {
    ResourceLocation identifier();

    ResourceLocation icon();

    int color();

    boolean inbuilt();
}
