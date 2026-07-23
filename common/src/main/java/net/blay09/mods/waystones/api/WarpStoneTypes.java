package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.block.BuiltinWarpStoneType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class WarpStoneTypes {
    public static final BuiltinWarpStoneType UNSCOPED = register(new BuiltinWarpStoneType("unscoped", WaystoneKinds.WAYSTONE, () -> Ingredient.of(Items.AIR)));
    public static final BuiltinWarpStoneType COPPER = register(new BuiltinWarpStoneType("copper", SharestoneTypes.COPPER.kind(), SharestoneTypes.COPPER::ingredient));
    public static final BuiltinWarpStoneType PRISMARINE = register(new BuiltinWarpStoneType("prismarine", SharestoneTypes.PRISMARINE.kind(), SharestoneTypes.PRISMARINE::ingredient));
    public static final BuiltinWarpStoneType GOLD = register(new BuiltinWarpStoneType("gold", SharestoneTypes.GOLD.kind(), SharestoneTypes.GOLD::ingredient));
    public static final BuiltinWarpStoneType DIAMOND = register(new BuiltinWarpStoneType("diamond", SharestoneTypes.DIAMOND.kind(), SharestoneTypes.DIAMOND::ingredient));
    public static final BuiltinWarpStoneType AMETHYST = register(new BuiltinWarpStoneType("amethyst", SharestoneTypes.AMETHYST.kind(), SharestoneTypes.AMETHYST::ingredient));
    public static final BuiltinWarpStoneType LAPIS = register(new BuiltinWarpStoneType("lapis", SharestoneTypes.LAPIS.kind(), SharestoneTypes.LAPIS::ingredient));
    public static final BuiltinWarpStoneType EMERALD = register(new BuiltinWarpStoneType("emerald", SharestoneTypes.EMERALD.kind(), SharestoneTypes.EMERALD::ingredient));
    public static final BuiltinWarpStoneType REDSTONE = register(new BuiltinWarpStoneType("redstone", SharestoneTypes.REDSTONE.kind(), SharestoneTypes.REDSTONE::ingredient));

    private static BuiltinWarpStoneType register(BuiltinWarpStoneType type) {
        return (BuiltinWarpStoneType) WarpStoneType.register(type);
    }

    public static Stream<BuiltinWarpStoneType> builtinValues() {
        return WarpStoneType.values().filter(it -> it instanceof BuiltinWarpStoneType).map(it -> (BuiltinWarpStoneType) it);
    }
}
