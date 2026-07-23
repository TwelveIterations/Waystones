package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinPortstoneType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class PortstoneTypes {
    public static final BuiltinPortstoneType UNSCOPED = register(new BuiltinPortstoneType("unscoped", DyeColor.WHITE, WaystoneKinds.WAYSTONE, () -> Ingredient.of(Items.AIR)));
    public static final BuiltinPortstoneType COPPER = register(new BuiltinPortstoneType("copper", SharestoneTypes.COPPER.color(), SharestoneTypes.COPPER.kind(), SharestoneTypes.COPPER::ingredient));
    public static final BuiltinPortstoneType PRISMARINE = register(new BuiltinPortstoneType("prismarine", SharestoneTypes.PRISMARINE.color(), SharestoneTypes.PRISMARINE.kind(), SharestoneTypes.PRISMARINE::ingredient));
    public static final BuiltinPortstoneType GOLD = register(new BuiltinPortstoneType("gold", SharestoneTypes.GOLD.color(), SharestoneTypes.GOLD.kind(), SharestoneTypes.GOLD::ingredient));
    public static final BuiltinPortstoneType DIAMOND = register(new BuiltinPortstoneType("diamond", SharestoneTypes.DIAMOND.color(), SharestoneTypes.DIAMOND.kind(), SharestoneTypes.DIAMOND::ingredient));
    public static final BuiltinPortstoneType AMETHYST = register(new BuiltinPortstoneType("amethyst", SharestoneTypes.AMETHYST.color(), SharestoneTypes.AMETHYST.kind(), SharestoneTypes.AMETHYST::ingredient));
    public static final BuiltinPortstoneType LAPIS = register(new BuiltinPortstoneType("lapis", SharestoneTypes.LAPIS.color(), SharestoneTypes.LAPIS.kind(), SharestoneTypes.LAPIS::ingredient));
    public static final BuiltinPortstoneType EMERALD = register(new BuiltinPortstoneType("emerald", SharestoneTypes.EMERALD.color(), SharestoneTypes.EMERALD.kind(), SharestoneTypes.EMERALD::ingredient));
    public static final BuiltinPortstoneType REDSTONE = register(new BuiltinPortstoneType("redstone", SharestoneTypes.REDSTONE.color(), SharestoneTypes.REDSTONE.kind(), SharestoneTypes.REDSTONE::ingredient));

    private static BuiltinPortstoneType register(BuiltinPortstoneType type) {
        return (BuiltinPortstoneType) PortstoneType.register(type);
    }

    public static Stream<BuiltinPortstoneType> builtinValues() {
        return PortstoneType.values().filter(it -> it instanceof BuiltinPortstoneType).map(it -> (BuiltinPortstoneType) it);
    }
}
