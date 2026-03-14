package net.blay09.mods.waystones.api;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;
import java.util.function.Supplier;

public enum SharestoneType implements StringRepresentable {
    COPPER(DyeColor.ORANGE, () -> Ingredient.of(Items.COPPER_INGOT)),
    PRISMARINE(DyeColor.LIGHT_BLUE, () -> Ingredient.of(Items.PRISMARINE_SHARD)),
    GOLD(DyeColor.YELLOW, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND(DyeColor.CYAN, () -> Ingredient.of(Items.DIAMOND)),
    AMETHYST(DyeColor.PURPLE, () -> Ingredient.of(Items.AMETHYST_SHARD)),
    LAPIS(DyeColor.BLUE, () -> Ingredient.of(Items.LAPIS_LAZULI)),
    EMERALD(DyeColor.GREEN, () -> Ingredient.of(Items.EMERALD)),
    REDSTONE(DyeColor.RED, () -> Ingredient.of(Items.REDSTONE));

    public static final StringRepresentable.EnumCodec<SharestoneType> CODEC = StringRepresentable.fromEnum(SharestoneType::values);

    private final DyeColor color;
    private final Supplier<Ingredient> ingredientSupplier;

    SharestoneType(DyeColor color, Supplier<Ingredient> ingredientSupplier) {
        this.color = color;
        this.ingredientSupplier = ingredientSupplier;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int getTextColor() {
        return color.getTextColor();
    }

    public int getTextureDiffuseColor() {
        return color.getTextureDiffuseColor();
    }

    public Ingredient getIngredient() {
        return ingredientSupplier.get();
    }

    @Override
    public String toString() {
        return getSerializedName();
    }
}
