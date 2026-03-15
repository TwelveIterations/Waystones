package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.SharestoneType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class BuiltinSharestoneType extends SharestoneType {

    private final String name;
    private final Supplier<Ingredient> ingredientSupplier;

    public BuiltinSharestoneType(String name, DyeColor color, Supplier<Ingredient> ingredientSupplier) {
        super(id(name + "_sharestone"), color);
        this.name = name;
        this.ingredientSupplier = ingredientSupplier;
    }

    public Ingredient ingredient() {
        return ingredientSupplier.get();
    }

    @Override
    public String toString() {
        return name;
    }
}
