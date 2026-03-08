package net.blay09.mods.waystones;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.commands.BalmCommands;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.balm.platform.config.BalmConfig;
import net.blay09.mods.balm.platform.module.BalmModule;
import net.blay09.mods.balm.platform.permissions.BalmPermissions;
import net.blay09.mods.balm.server.packs.resources.BalmResourceConditionRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.balm.world.level.levelgen.BalmWorldGen;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.blay09.mods.waystones.command.ModCommands;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.compat.hudinfo.WarpPlateBlockInfoProvider;
import net.blay09.mods.waystones.compat.hudinfo.WaystoneBlockInfoProvider;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.handler.ModEventHandlers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.network.ModNetworking;
import net.blay09.mods.waystones.permission.ModPermissions;
import net.blay09.mods.waystones.resources.ForceSpawnInVillagesCondition;
import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.blay09.mods.waystones.store.WaystonesStore;
import net.blay09.mods.waystones.worldgen.ModWorldGen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waystones implements BalmModule {

    public static final Logger logger = LoggerFactory.getLogger(Waystones.class);

    public static final String MOD_ID = "waystones";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static WaystonesStore getWaystoneStore(Player player) {
        if (player instanceof ServerPlayer) {
            return SavedDataWaystonesStore.get(player.level().getServer());
        } else {
            return WaystonesClient.getWaystonesStore();
        }
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "common");
    }

    @Override
    public void registerConfig(BalmConfig config) {
        config.registerConfig(WaystonesConfig.class);
    }

    @Override
    public void registerCustomStats(BalmCustomStatRegistrar stats) {
        ModStats.initialize(stats);
    }

    @Override
    public void registerNetworking(BalmNetworking networking) {
        ModNetworking.initialize(networking);
    }

    @Override
    public void registerBlocks(BalmBlockRegistrar blocks) {
        ModBlocks.initialize(blocks);
    }

    @Override
    public void registerBlockEntityTypes(BalmBlockEntityTypeRegistrar blockEntities) {
        ModBlockEntities.initialize(blockEntities);
    }

    @Override
    public void registerItems(BalmItemRegistrar items) {
        ModItems.initialize(items);
    }

    @Override
    public void registerCreativeModeTabs(BalmCreativeModeTabRegistrar creativeModeTabs) {
        ModItems.initialize(creativeModeTabs);
    }

    @Override
    public void registerMenuTypes(BalmMenuTypeRegistrar menus) {
        ModMenus.initialize(menus);
    }

    @Override
    public void registerWorldGen(BalmWorldGen worldGen) {
        ModWorldGen.initialize(worldGen);
    }

    @Override
    public void registerAdditional(BalmRegistrar registrar) {
        ModWorldGen.initializeFeatures(registrar.scoped(Registries.FEATURE, getId().getNamespace()));
        ModWorldGen.initializePlacementModifierTypes(registrar.scoped(Registries.PLACEMENT_MODIFIER_TYPE, getId().getNamespace()));
    }

    @Override
    public void registerPoiTypes(BalmPoiTypeRegistrar poiTypes) {
        ModWorldGen.initializePoiTypes(poiTypes);
    }

    @Override
    public void registerCommands(BalmCommands commands) {
        ModCommands.initialize(commands);
    }

    @Override
    public void registerDataComponentTypes(BalmDataComponentTypeRegistrar components) {
        ModComponents.initialize(components);
    }

    @Override
    public void registerResourceConditions(BalmResourceConditionRegistrar resources) {
        resources.register("force_spawn_in_villages", ForceSpawnInVillagesCondition.CODEC);
    }

    @Override
    public void registerPermissions(BalmPermissions permissions) {
        ModPermissions.initialize(permissions);
    }

    @Override
    public void initialize() {
        ModEventHandlers.initialize();
        WaystonesRules.initialize();

        final var hudInfo = Balm.modSupport().hudInfo();
        hudInfo.registerBlockInfo(id("waystone"), ModBlocks.waystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("sandy_waystone"), ModBlocks.sandyWaystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("mossy_waystone"), ModBlocks.mossyWaystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("blackstone_waystone"), ModBlocks.blackstoneWaystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("deepslate_waystone"), ModBlocks.deepslateWaystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("end_stone_waystone"), ModBlocks.endStoneWaystone, new WaystoneBlockInfoProvider());
        hudInfo.registerBlockInfo(id("warp_plate"), ModBlocks.warpPlate, new WarpPlateBlockInfoProvider());
        ModBlocks.sharestones.forEach((color, block) -> hudInfo.registerBlockInfo(block.unwrapKey().map(ResourceKey::identifier).orElseThrow(), block, new WaystoneBlockInfoProvider()));

        Balm.initializeIfLoaded("bluemap", "net.blay09.mods.waystones.compat.BlueMapIntegration");
        Balm.initializeIfLoaded("dynmap", "net.blay09.mods.waystones.compat.DynmapIntegration");
        Balm.initializeIfLoaded(Compat.UNBREAKABLES, "net.blay09.mods.waystones.compat.UnbreakablesIntegration");
    }

}
