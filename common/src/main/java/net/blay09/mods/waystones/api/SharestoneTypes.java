package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinSharestoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class SharestoneTypes {
    public static final BuiltinSharestoneType RUINED = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("ruined", 0xffffff, () -> Ingredient.of(Items.AIR), () -> ModBlocks.sharestones.get(SharestoneTypes.RUINED).asBlock()));
    public static final BuiltinSharestoneType COPPER = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("copper", 0xff681f, () -> Ingredient.of(Items.COPPER_INGOT), () -> ModBlocks.sharestones.get(SharestoneTypes.COPPER).asBlock()));
    public static final BuiltinSharestoneType PRISMARINE = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("prismarine", 0x9ac0cd, () -> Ingredient.of(Items.PRISMARINE_SHARD), () -> ModBlocks.sharestones.get(SharestoneTypes.PRISMARINE).asBlock()));
    public static final BuiltinSharestoneType GOLD = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("gold", 0xffff00, () -> Ingredient.of(Items.GOLD_INGOT), () -> ModBlocks.sharestones.get(SharestoneTypes.GOLD).asBlock()));
    public static final BuiltinSharestoneType DIAMOND = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("diamond", 0x808080, () -> Ingredient.of(Items.DIAMOND), () -> ModBlocks.sharestones.get(SharestoneTypes.DIAMOND).asBlock()));
    public static final BuiltinSharestoneType AMETHYST = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("amethyst", 0xa020f0, () -> Ingredient.of(Items.AMETHYST_SHARD), () -> ModBlocks.sharestones.get(SharestoneTypes.AMETHYST).asBlock()));
    public static final BuiltinSharestoneType LAPIS = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("lapis", 0x0000ff, () -> Ingredient.of(Items.LAPIS_LAZULI), () -> ModBlocks.sharestones.get(SharestoneTypes.LAPIS).asBlock()));
    public static final BuiltinSharestoneType EMERALD = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("emerald", 0x00ff00, () -> Ingredient.of(Items.EMERALD), () -> ModBlocks.sharestones.get(SharestoneTypes.EMERALD).asBlock()));
    public static final BuiltinSharestoneType REDSTONE = (BuiltinSharestoneType) SharestoneType.register(new BuiltinSharestoneType("redstone", 0xff0000, () -> Ingredient.of(Items.REDSTONE), () -> ModBlocks.sharestones.get(SharestoneTypes.REDSTONE).asBlock()));

    public static Stream<BuiltinSharestoneType> builtinValues() {
        return SharestoneType.values().filter(it -> it instanceof BuiltinSharestoneType).map(it -> (BuiltinSharestoneType) it);
    }
}
