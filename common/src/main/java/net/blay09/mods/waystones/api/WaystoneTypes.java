package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinWaystoneType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

import java.util.stream.Stream;

public class WaystoneTypes {
    public static final BuiltinWaystoneType ANDESITE = register(new BuiltinWaystoneType("andesite", 0xFFFFFFFF, SoundType.STONE, Blocks.STONE_BRICKS, () -> Ingredient.of(Blocks.STONE_BRICKS)));
    public static final BuiltinWaystoneType MOSSY_ANDESITE = register(new BuiltinWaystoneType("mossy_andesite", 0xFFFFFFFF, SoundType.STONE, Blocks.MOSSY_STONE_BRICKS, () -> Ingredient.of(Blocks.MOSSY_STONE_BRICKS)));
    public static final BuiltinWaystoneType SANDSTONE = register(new BuiltinWaystoneType("sandstone", 0xFFFFFFFF, SoundType.SAND, Blocks.CHISELED_SANDSTONE, () -> Ingredient.of(Blocks.CHISELED_SANDSTONE)));
    public static final BuiltinWaystoneType DEEPSLATE = register(new BuiltinWaystoneType("deepslate", 0xFFFFFFFF, SoundType.DEEPSLATE, Blocks.DEEPSLATE, () -> Ingredient.of(Blocks.DEEPSLATE)));
    public static final BuiltinWaystoneType BLACKSTONE = register(new BuiltinWaystoneType("blackstone", 0xFF993333, SoundType.STONE, Blocks.BLACKSTONE, () -> Ingredient.of(Blocks.BLACKSTONE)));
    public static final BuiltinWaystoneType END_STONE = register(new BuiltinWaystoneType("end_stone", 0xFF7200FF, SoundType.STONE, Blocks.END_STONE_BRICKS, () -> Ingredient.of(Blocks.END_STONE_BRICKS)));
    public static final BuiltinWaystoneType RED_NETHER_BRICKS = register(new BuiltinWaystoneType("red_nether_bricks", 0xFFFFFFFF, SoundType.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, () -> Ingredient.of(Items.EMERALD)));
    public static final BuiltinWaystoneType PURPUR = register(new BuiltinWaystoneType("purpur", 0xFFFFFFFF, SoundType.STONE, Blocks.PURPUR_BLOCK, () -> Ingredient.of(Items.REDSTONE)));
    public static final BuiltinWaystoneType PRISMARINE = register(new BuiltinWaystoneType("prismarine", 0xFFFFFFFF, SoundType.STONE, Blocks.PRISMARINE, () -> Ingredient.of(Items.REDSTONE)));
    public static final BuiltinWaystoneType MUD_BRICKS = register(new BuiltinWaystoneType("mud_bricks", 0xFFFFFFFF, SoundType.MUD_BRICKS, Blocks.MUD_BRICKS, () -> Ingredient.of(Items.REDSTONE)));

    private static BuiltinWaystoneType register(BuiltinWaystoneType type) {
        return (BuiltinWaystoneType) WaystoneType.register(type);
    }

    public static Stream<BuiltinWaystoneType> builtinValues() {
        return WaystoneType.values().filter(it -> it instanceof BuiltinWaystoneType).map(it -> (BuiltinWaystoneType) it);
    }
}
