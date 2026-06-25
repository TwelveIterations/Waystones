package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

public interface WaystoneGroup {
    Identifier identifier();

    Component name();

    Identifier icon();

    int color();

    boolean inbuilt();

    boolean hidden();
}
