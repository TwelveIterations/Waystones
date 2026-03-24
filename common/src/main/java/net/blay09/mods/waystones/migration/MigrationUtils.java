package net.blay09.mods.waystones.migration;

import com.mojang.serialization.DataResult;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.blay09.mods.waystones.Waystones.id;

public class MigrationUtils {

    private static final Map<Identifier, Identifier> waystoneKindMigrations = new ConcurrentHashMap<>();

    static {
        waystoneKindMigrations.put(id("orange_sharestone"), id("copper_sharestone"));
        waystoneKindMigrations.put(id("magenta_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("light_blue_sharestone"), id("prismarine_sharestone"));
        waystoneKindMigrations.put(id("yellow_sharestone"), id("gold_sharestone"));
        waystoneKindMigrations.put(id("lime_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("pink_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("gray_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("light_gray_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("cyan_sharestone"), id("diamond_sharestone"));
        waystoneKindMigrations.put(id("purple_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("blue_sharestone"), id("lapis_sharestone"));
        waystoneKindMigrations.put(id("brown_sharestone"), id("ruined_sharestone"));
        waystoneKindMigrations.put(id("green_sharestone"), id("emerald_sharestone"));
        waystoneKindMigrations.put(id("red_sharestone"), id("redstone_sharestone"));
        waystoneKindMigrations.put(id("black_sharestone"), id("ruined_sharestone"));
    }

    public static DataResult<Identifier> migrateWaystoneKind(Identifier identifier) {
        return DataResult.success(waystoneKindMigrations.getOrDefault(identifier, identifier));
    }

    public static void migrateBlocks(BalmBlockRegistrar blocks) {
        blocks.addAlias("waystone", "andesite_waystone");
        blocks.addAlias("sandy_waystone", "sandstone_waystone");
        blocks.addAlias("mossy_waystone", "mossy_andesite_waystone");

        blocks.addAlias("orange_sharestone", "copper_sharestone");
        blocks.addAlias("magenta_sharestone", "ruined_sharestone");
        blocks.addAlias("light_blue_sharestone", "prismarine_sharestone");
        blocks.addAlias("yellow_sharestone", "gold_sharestone");
        blocks.addAlias("lime_sharestone", "ruined_sharestone");
        blocks.addAlias("pink_sharestone", "ruined_sharestone");
        blocks.addAlias("gray_sharestone", "ruined_sharestone");
        blocks.addAlias("light_gray_sharestone", "ruined_sharestone");
        blocks.addAlias("cyan_sharestone", "diamond_sharestone");
        blocks.addAlias("purple_sharestone", "ruined_sharestone");
        blocks.addAlias("blue_sharestone", "lapis_sharestone");
        blocks.addAlias("brown_sharestone", "ruined_sharestone");
        blocks.addAlias("green_sharestone", "emerald_sharestone");
        blocks.addAlias("red_sharestone", "redstone_sharestone");
        blocks.addAlias("black_sharestone", "ruined_sharestone");

        blocks.addAlias("orange_portstone", "copper_portstone");
        blocks.addAlias("magenta_portstone", "portstone");
        blocks.addAlias("light_blue_portstone", "prismarine_portstone");
        blocks.addAlias("yellow_portstone", "gold_portstone");
        blocks.addAlias("lime_portstone", "portstone");
        blocks.addAlias("pink_portstone", "portstone");
        blocks.addAlias("gray_portstone", "portstone");
        blocks.addAlias("light_gray_portstone", "portstone");
        blocks.addAlias("cyan_portstone", "diamond_portstone");
        blocks.addAlias("purple_portstone", "portstone");
        blocks.addAlias("blue_portstone", "lapis_portstone");
        blocks.addAlias("brown_portstone", "portstone");
        blocks.addAlias("green_portstone", "emerald_portstone");
        blocks.addAlias("red_portstone", "redstone_portstone");
        blocks.addAlias("black_portstone", "portstone");
    }

    public static void migrateItems(BalmItemRegistrar items) {
        items.addAlias("waystone", "andesite_waystone");
        items.addAlias("sandy_waystone", "sandstone_waystone");
        items.addAlias("mossy_waystone", "mossy_andesite_waystone");

        items.addAlias("orange_sharestone", "copper_sharestone");
        items.addAlias("magenta_sharestone", "ruined_sharestone");
        items.addAlias("light_blue_sharestone", "prismarine_sharestone");
        items.addAlias("yellow_sharestone", "gold_sharestone");
        items.addAlias("lime_sharestone", "ruined_sharestone");
        items.addAlias("pink_sharestone", "ruined_sharestone");
        items.addAlias("gray_sharestone", "ruined_sharestone");
        items.addAlias("light_gray_sharestone", "ruined_sharestone");
        items.addAlias("cyan_sharestone", "diamond_sharestone");
        items.addAlias("purple_sharestone", "ruined_sharestone");
        items.addAlias("blue_sharestone", "lapis_sharestone");
        items.addAlias("brown_sharestone", "ruined_sharestone");
        items.addAlias("green_sharestone", "emerald_sharestone");
        items.addAlias("red_sharestone", "redstone_sharestone");
        items.addAlias("black_sharestone", "ruined_sharestone");

        items.addAlias("orange_portstone", "copper_portstone");
        items.addAlias("magenta_portstone", "portstone");
        items.addAlias("light_blue_portstone", "prismarine_portstone");
        items.addAlias("yellow_portstone", "gold_portstone");
        items.addAlias("lime_portstone", "portstone");
        items.addAlias("pink_portstone", "portstone");
        items.addAlias("gray_portstone", "portstone");
        items.addAlias("light_gray_portstone", "portstone");
        items.addAlias("cyan_portstone", "diamond_portstone");
        items.addAlias("purple_portstone", "portstone");
        items.addAlias("blue_portstone", "lapis_portstone");
        items.addAlias("brown_portstone", "portstone");
        items.addAlias("green_portstone", "emerald_portstone");
        items.addAlias("red_portstone", "redstone_portstone");
        items.addAlias("black_portstone", "portstone");
    }
}
