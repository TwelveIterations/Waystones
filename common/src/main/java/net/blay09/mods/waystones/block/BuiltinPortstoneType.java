package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.PortstoneType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class BuiltinPortstoneType extends PortstoneType {

    private final String name;
    private final Supplier<Ingredient> ingredientSupplier;

    public BuiltinPortstoneType(String name, DyeColor color, Identifier kind, Supplier<Ingredient> ingredientSupplier) {
        super(id(StringUtil.isBlank(name) ? "unscoped" : name), color, kind);
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
