package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinWaystoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

import java.util.stream.Stream;

public class WaystoneTypes {
    public static final BuiltinWaystoneType ANDESITE = register(new BuiltinWaystoneType("andesite", 0xFFFFFFFF, SoundType.STONE, Blocks.STONE_BRICKS, () -> Ingredient.of(Blocks.STONE_BRICKS), () -> ModBlocks.waystones.get(WaystoneTypes.ANDESITE).asBlock()));
    public static final BuiltinWaystoneType MOSSY_ANDESITE = register(new BuiltinWaystoneType("mossy_andesite", 0xFFFFFFFF, SoundType.STONE, Blocks.MOSSY_STONE_BRICKS, () -> Ingredient.of(Blocks.MOSSY_STONE_BRICKS), () -> ModBlocks.waystones.get(WaystoneTypes.MOSSY_ANDESITE).asBlock()));
    public static final BuiltinWaystoneType SANDSTONE = register(new BuiltinWaystoneType("sandstone", 0xFFFFFFFF, SoundType.SAND, Blocks.CHISELED_SANDSTONE, () -> Ingredient.of(Blocks.CHISELED_SANDSTONE), () -> ModBlocks.waystones.get(WaystoneTypes.SANDSTONE).asBlock()));
    public static final BuiltinWaystoneType DEEPSLATE = register(new BuiltinWaystoneType("deepslate", 0xFFFFFFFF, SoundType.DEEPSLATE, Blocks.DEEPSLATE, () -> Ingredient.of(Blocks.DEEPSLATE), () -> ModBlocks.waystones.get(WaystoneTypes.DEEPSLATE).asBlock()));
    public static final BuiltinWaystoneType BLACKSTONE = register(new BuiltinWaystoneType("blackstone", 0xFF993333, SoundType.STONE, Blocks.BLACKSTONE, () -> Ingredient.of(Blocks.BLACKSTONE), () -> ModBlocks.waystones.get(WaystoneTypes.BLACKSTONE).asBlock()));
    public static final BuiltinWaystoneType END_STONE = register(new BuiltinWaystoneType("end_stone", 0xFF7200FF, SoundType.STONE, Blocks.END_STONE_BRICKS, () -> Ingredient.of(Blocks.END_STONE_BRICKS), () -> ModBlocks.waystones.get(WaystoneTypes.END_STONE).asBlock()));
    public static final BuiltinWaystoneType RED_NETHER_BRICKS = register(new BuiltinWaystoneType("red_nether_bricks", 0xFFFFFFFF, SoundType.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, () -> Ingredient.of(Items.RED_NETHER_BRICKS), () -> ModBlocks.waystones.get(WaystoneTypes.RED_NETHER_BRICKS).asBlock()));
    public static final BuiltinWaystoneType PURPUR = register(new BuiltinWaystoneType("purpur", 0xFFFFFFFF, SoundType.STONE, Blocks.PURPUR_BLOCK, () -> Ingredient.of(Blocks.PURPUR_BLOCK), () -> ModBlocks.waystones.get(WaystoneTypes.PURPUR).asBlock()));
    public static final BuiltinWaystoneType PRISMARINE = register(new BuiltinWaystoneType("prismarine", 0xFFFFFFFF, SoundType.STONE, Blocks.PRISMARINE, () -> Ingredient.of(Blocks.PRISMARINE), () -> ModBlocks.waystones.get(WaystoneTypes.PRISMARINE).asBlock()));
    public static final BuiltinWaystoneType MUD_BRICKS = register(new BuiltinWaystoneType("mud_bricks", 0xFFFFFFFF, SoundType.MUD_BRICKS, Blocks.MUD_BRICKS, () -> Ingredient.of(Blocks.MUD_BRICKS), () -> ModBlocks.waystones.get(WaystoneTypes.MUD_BRICKS).asBlock()));

    private static BuiltinWaystoneType register(BuiltinWaystoneType type) {
        return (BuiltinWaystoneType) WaystoneType.register(type);
    }

    public static Stream<BuiltinWaystoneType> builtinValues() {
        return WaystoneType.values().filter(it -> it instanceof BuiltinWaystoneType).map(it -> (BuiltinWaystoneType) it);
    }
}
