package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;

/**
 * @see WaystoneStyles
 */
public class WaystoneStyle {

    private final Identifier blockRegistryName;
    private int runeColor = 0xFFFFFFFF;

    public WaystoneStyle(Identifier blockRegistryName) {
        this.blockRegistryName = blockRegistryName;
    }

    public Identifier getBlockRegistryName() {
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
