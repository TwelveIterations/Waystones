package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;

public interface WaystoneGroup {
    Identifier identifier();

    Identifier icon();

    int color();

    boolean inbuilt();
}
