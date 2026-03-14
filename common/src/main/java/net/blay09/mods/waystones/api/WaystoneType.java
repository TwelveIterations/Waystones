package net.blay09.mods.waystones.api;

import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public enum WaystoneType implements StringRepresentable {
    ANDESITE(Identifier.fromNamespaceAndPath("waystones", "waystone"), SoundType.STONE, 0xFFFFFFFF, () -> Ingredient.of(Blocks.STONE_BRICKS)),
    MOSSY(Identifier.fromNamespaceAndPath("waystones", "mossy_waystone"), SoundType.STONE, 0xFFFFFFFF, () -> Ingredient.of(Blocks.MOSSY_STONE_BRICKS)),
    SANDY(Identifier.fromNamespaceAndPath("waystones", "sandy_waystone"), SoundType.SAND, 0xFFFFFFFF, () -> Ingredient.of(Blocks.CHISELED_SANDSTONE)),
    DEEPSLATE(Identifier.fromNamespaceAndPath("waystones", "deepslate_waystone"), SoundType.DEEPSLATE, 0xFFFFFFFF, () -> Ingredient.of(Blocks.DEEPSLATE)),
    BLACKSTONE(Identifier.fromNamespaceAndPath("waystones", "blackstone_waystone"), SoundType.STONE, 0xFF993333, () -> Ingredient.of(Blocks.BLACKSTONE)),
    END_STONE(Identifier.fromNamespaceAndPath("waystones", "end_stone_waystone"), SoundType.STONE, 0xFF7200FF, () -> Ingredient.of(Blocks.END_STONE_BRICKS)),
    RED_NETHER_BRICKS(Identifier.fromNamespaceAndPath("waystones", "red_nether_bricks_waystone"), SoundType.NETHER_BRICKS, 0xFFFFFFFF, () -> Ingredient.of(Items.EMERALD)),
    PURPUR(Identifier.fromNamespaceAndPath("waystones", "purpur_waystone"), SoundType.STONE, 0xFFFFFFFF, () -> Ingredient.of(Items.REDSTONE)),
    PRISMARINE(Identifier.fromNamespaceAndPath("waystones", "prismarine_waystone"), SoundType.STONE, 0xFFFFFFFF, () -> Ingredient.of(Items.REDSTONE)),
    MUD_BRICKS(Identifier.fromNamespaceAndPath("waystones", "mud_bricks_waystone"), SoundType.MUD_BRICKS, 0xFFFFFFFF, () -> Ingredient.of(Items.REDSTONE));

    public static final Map<Identifier, WaystoneType> BY_IDENTIFIER = new ConcurrentHashMap<>();
    public static final EnumCodec<WaystoneType> CODEC = StringRepresentable.fromEnum(WaystoneType::values);

    private final Identifier identifier;
    private final SoundType soundType;
    private final int runeColor;
    private final Supplier<Ingredient> ingredientSupplier;

    WaystoneType(Identifier identifier, SoundType soundType, int runeColor, Supplier<Ingredient> ingredientSupplier) {
        this.identifier = identifier;
        this.soundType = soundType;
        this.runeColor = runeColor;
        this.ingredientSupplier = ingredientSupplier;
    }

    @Nullable
    public static WaystoneType getType(Identifier typeId) {
        return BY_IDENTIFIER.get(typeId);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public int getRuneColor() {
        return runeColor;
    }

    public Ingredient getIngredient() {
        return ingredientSupplier.get();
    }

    @Override
    public String toString() {
        return getSerializedName();
    }
}
