package net.blay09.mods.waystones.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WaystoneStyles {
    private static final Map<Identifier, WaystoneStyle> styles = new HashMap<>();
    private static final Map<Block, WaystoneStyle> stylesByBlock = new HashMap<>();

    public static WaystoneStyle DEFAULT = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "waystone")));
    public static WaystoneStyle MOSSY = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "mossy_waystone")));
    public static WaystoneStyle SANDY = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "sandy_waystone")));
    public static WaystoneStyle BLACKSTONE = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "blackstone_waystone")).withRuneColor(0xFF993333));
    public static WaystoneStyle DEEPSLATE = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "deepslate_waystone")));
    public static WaystoneStyle END_STONE = register(new WaystoneStyle(Identifier.fromNamespaceAndPath("waystones", "end_stone_waystone")).withRuneColor(0xFF7200FF));

    public static WaystoneStyle register(WaystoneStyle style) {
        styles.put(style.getBlockRegistryName(), style);
        return style;
    }

    @Nullable
    public static WaystoneStyle getStyle(Block block) {
        return stylesByBlock.computeIfAbsent(block, key -> getStyle(BuiltInRegistries.BLOCK.getKey(block)));
    }

    @Nullable
    public static WaystoneStyle getStyle(Identifier name) {
        return styles.get(name);
    }

    public static Collection<Identifier> getRegisteredKeys() {
        return styles.keySet();
    }
}
