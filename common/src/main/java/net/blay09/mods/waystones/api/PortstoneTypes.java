package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinPortstoneType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class PortstoneTypes {
    public static final BuiltinPortstoneType UNSCOPED = register(new BuiltinPortstoneType("unscoped", DyeColor.WHITE, WaystoneKinds.WAYSTONE, () -> Ingredient.of(Items.AIR)));
    public static final BuiltinPortstoneType COPPER = register(new BuiltinPortstoneType("copper", DyeColor.ORANGE, SharestoneTypes.COPPER.kind(), SharestoneTypes.COPPER::ingredient));
    public static final BuiltinPortstoneType PRISMARINE = register(new BuiltinPortstoneType("prismarine", DyeColor.LIGHT_BLUE, SharestoneTypes.PRISMARINE.kind(), SharestoneTypes.PRISMARINE::ingredient));
    public static final BuiltinPortstoneType GOLD = register(new BuiltinPortstoneType("gold", DyeColor.YELLOW, SharestoneTypes.GOLD.kind(), SharestoneTypes.GOLD::ingredient));
    public static final BuiltinPortstoneType DIAMOND = register(new BuiltinPortstoneType("diamond", DyeColor.CYAN, SharestoneTypes.DIAMOND.kind(), SharestoneTypes.DIAMOND::ingredient));
    public static final BuiltinPortstoneType AMETHYST = register(new BuiltinPortstoneType("amethyst", DyeColor.PURPLE, SharestoneTypes.AMETHYST.kind(), SharestoneTypes.AMETHYST::ingredient));
    public static final BuiltinPortstoneType LAPIS = register(new BuiltinPortstoneType("lapis", DyeColor.BLUE, SharestoneTypes.LAPIS.kind(), SharestoneTypes.LAPIS::ingredient));
    public static final BuiltinPortstoneType EMERALD = register(new BuiltinPortstoneType("emerald", DyeColor.GREEN, SharestoneTypes.EMERALD.kind(), SharestoneTypes.EMERALD::ingredient));
    public static final BuiltinPortstoneType REDSTONE = register(new BuiltinPortstoneType("redstone", DyeColor.RED, SharestoneTypes.REDSTONE.kind(), SharestoneTypes.REDSTONE::ingredient));

    private static BuiltinPortstoneType register(BuiltinPortstoneType type) {
        return (BuiltinPortstoneType) PortstoneType.register(type);
    }

    public static Stream<BuiltinPortstoneType> builtinValues() {
        return PortstoneType.values().filter(it -> it instanceof BuiltinPortstoneType).map(it -> (BuiltinPortstoneType) it);
    }
}
