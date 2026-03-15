package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.WarpStoneType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class BuiltinWarpStoneType extends WarpStoneType {

    private final String name;
    private final Supplier<Ingredient> ingredientSupplier;

    public BuiltinWarpStoneType(String name, Identifier kind, Supplier<Ingredient> ingredientSupplier) {
        super(id(name), kind);
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
