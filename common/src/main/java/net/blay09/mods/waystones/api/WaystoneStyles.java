package net.blay09.mods.waystones.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WaystoneStyles {

    private static final Map<ResourceLocation, WaystoneStyle> styles = new HashMap<>();
    private static final Map<Block, WaystoneStyle> stylesByBlock = new HashMap<>();

    public static WaystoneStyle DEFAULT = register(new WaystoneStyle(new ResourceLocation("waystones", "waystone")));
    public static WaystoneStyle MOSSY = register(new WaystoneStyle(new ResourceLocation("waystones", "mossy_waystone")));
    public static WaystoneStyle SANDY = register(new WaystoneStyle(new ResourceLocation("waystones", "sandy_waystone")));
    public static WaystoneStyle BLACKSTONE = register(new WaystoneStyle(new ResourceLocation("waystones", "blackstone_waystone")).withRuneColor(0xFF993333));
    public static WaystoneStyle DEEPSLATE = register(new WaystoneStyle(new ResourceLocation("waystones", "deepslate_waystone")));
    public static WaystoneStyle END_STONE = register(new WaystoneStyle(new ResourceLocation("waystones", "end_stone_waystone")).withRuneColor(0xFF7200FF));

    public static WaystoneStyle register(WaystoneStyle style) {
        styles.put(style.getBlockRegistryName(), style);
        return style;
    }

    @Nullable
    public static WaystoneStyle getStyle(Block block) {
        return stylesByBlock.computeIfAbsent(block, key -> getStyle(BuiltInRegistries.BLOCK.getKey(block)));
    }

    @Nullable
    public static WaystoneStyle getStyle(ResourceLocation name) {
        return styles.get(name);
    }

    public static Collection<ResourceLocation> getRegisteredKeys() {
        return styles.keySet();
    }
}
