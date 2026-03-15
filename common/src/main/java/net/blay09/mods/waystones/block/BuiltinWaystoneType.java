package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.WaystoneType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class BuiltinWaystoneType extends WaystoneType {

    private final String name;
    private final SoundType soundType;
    private final Block particleBlock;
    private final Supplier<Ingredient> ingredientSupplier;

    public BuiltinWaystoneType(String name, int runeColor, SoundType soundType, Block particleBlock, Supplier<Ingredient> ingredientSupplier) {
        super(id(name + "_waystone"), runeColor);
        this.name = name;
        this.soundType = soundType;
        this.particleBlock = particleBlock;
        this.ingredientSupplier = ingredientSupplier;
    }

    public Ingredient ingredient() {
        return ingredientSupplier.get();
    }

    @Override
    public String toString() {
        return name;
    }

    public Block particleBlock() {
        return particleBlock;
    }

    public SoundType soundType() {
        return soundType;
    }
}
