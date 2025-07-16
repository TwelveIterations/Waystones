package net.blay09.mods.waystones.api;

import net.minecraft.resources.ResourceLocation;

/**
 * @see WaystoneStyles
 */
public class WaystoneStyle {

    private final ResourceLocation blockRegistryName;
    private int runeColor = 0xFFFFFFFF;

    public WaystoneStyle(ResourceLocation blockRegistryName) {
        this.blockRegistryName = blockRegistryName;
    }

    public ResourceLocation getBlockRegistryName() {
        return blockRegistryName;
    }

    public int getRuneColor() {
        return runeColor;
    }

    public WaystoneStyle withRuneColor(int runeColor) {
        this.runeColor = runeColor;
        return this;
    }
}
