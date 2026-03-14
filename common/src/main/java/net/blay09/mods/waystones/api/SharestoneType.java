package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;
import java.util.function.Supplier;

public enum SharestoneType implements StringRepresentable {
    RUINED(Identifier.fromNamespaceAndPath("waystones", "ruined_sharestone"), DyeColor.BLACK, () -> Ingredient.of(Items.AIR)),
    COPPER(Identifier.fromNamespaceAndPath("waystones", "copper_sharestone"), DyeColor.ORANGE, () -> Ingredient.of(Items.COPPER_INGOT)),
    PRISMARINE(Identifier.fromNamespaceAndPath("waystones", "prismarine_sharestone"), DyeColor.LIGHT_BLUE, () -> Ingredient.of(Items.PRISMARINE_SHARD)),
    GOLD(Identifier.fromNamespaceAndPath("waystones", "gold_sharestone"), DyeColor.YELLOW, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND(Identifier.fromNamespaceAndPath("waystones", "diamond_sharestone"), DyeColor.CYAN, () -> Ingredient.of(Items.DIAMOND)),
    AMETHYST(Identifier.fromNamespaceAndPath("waystones", "amethyst_sharestone"), DyeColor.PURPLE, () -> Ingredient.of(Items.AMETHYST_SHARD)),
    LAPIS(Identifier.fromNamespaceAndPath("waystones", "lapis_sharestone"), DyeColor.BLUE, () -> Ingredient.of(Items.LAPIS_LAZULI)),
    EMERALD(Identifier.fromNamespaceAndPath("waystones", "emerald_sharestone"), DyeColor.GREEN, () -> Ingredient.of(Items.EMERALD)),
    REDSTONE(Identifier.fromNamespaceAndPath("waystones", "redstone_sharestone"), DyeColor.RED, () -> Ingredient.of(Items.REDSTONE));

    public static final StringRepresentable.EnumCodec<SharestoneType> CODEC = StringRepresentable.fromEnum(SharestoneType::values);

    private final Identifier identifier;
    private final DyeColor color;
    private final Supplier<Ingredient> ingredientSupplier;

    SharestoneType(Identifier identifier, DyeColor color, Supplier<Ingredient> ingredientSupplier) {
        this.identifier = identifier;
        this.color = color;
        this.ingredientSupplier = ingredientSupplier;
    }

    public Identifier getIdentifier() {
        return identifier;
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
