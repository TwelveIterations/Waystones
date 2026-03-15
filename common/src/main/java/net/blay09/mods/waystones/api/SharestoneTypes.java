package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinSharestoneType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class SharestoneTypes {
    public static final BuiltinSharestoneType RUINED = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("ruined", DyeColor.BLACK, () -> Ingredient.of(Items.AIR)));
    public static final BuiltinSharestoneType COPPER = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("copper", DyeColor.ORANGE, () -> Ingredient.of(Items.COPPER_INGOT)));
    public static final BuiltinSharestoneType PRISMARINE = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("prismarine", DyeColor.LIGHT_BLUE, () -> Ingredient.of(Items.PRISMARINE_SHARD)));
    public static final BuiltinSharestoneType GOLD = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("gold", DyeColor.YELLOW, () -> Ingredient.of(Items.GOLD_INGOT)));
    public static final BuiltinSharestoneType DIAMOND = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("diamond", DyeColor.CYAN, () -> Ingredient.of(Items.DIAMOND)));
    public static final BuiltinSharestoneType AMETHYST = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("amethyst", DyeColor.PURPLE, () -> Ingredient.of(Items.AMETHYST_SHARD)));
    public static final BuiltinSharestoneType LAPIS = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("lapis", DyeColor.BLUE, () -> Ingredient.of(Items.LAPIS_LAZULI)));
    public static final BuiltinSharestoneType EMERALD = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("emerald", DyeColor.GREEN, () -> Ingredient.of(Items.EMERALD)));
    public static final BuiltinSharestoneType REDSTONE = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("redstone", DyeColor.RED, () -> Ingredient.of(Items.REDSTONE)));

    public static Stream<BuiltinSharestoneType> builtinValues() {
        return SharestoneType.values().filter(it -> it instanceof BuiltinSharestoneType).map(it -> (BuiltinSharestoneType) it);
    }
}
