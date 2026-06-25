package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface WaystoneGroup {
    ResourceLocation identifier();

    Component name();

    ResourceLocation icon();

    int color();

    boolean inbuilt();

    boolean hidden();
}
