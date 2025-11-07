package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.balm.api.config.BalmConfig;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.module.BalmModule;
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.permission.BalmPermissions;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.server.packs.resources.BalmResourceConditionRegistrar;
import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.command.ModCommands;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.permission.ModPermissions;
import net.blay09.mods.waystones.requirement.RequirementRegistry;
import net.blay09.mods.waystones.handler.ModEventHandlers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.network.ModNetworking;
import net.blay09.mods.waystones.resources.ForceSpawnInVillagesCondition;
import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.worldgen.ModWorldGen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waystones implements BalmModule {

    public static final Logger logger = LoggerFactory.getLogger(Waystones.class);

    public static final String MOD_ID = "waystones";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "common");
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
    public void registerEvents(BalmEvents events) {
        ModEventHandlers.initialize(events);
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
        ModWorldGen.initializePoiTypes(registrar.scoped(Registries.POINT_OF_INTEREST_TYPE, getId().getNamespace()));
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
        RequirementRegistry.registerDefaults();

        Balm.initializeIfLoaded("bluemap", "net.blay09.mods.waystones.compat.BlueMapIntegration");
        Balm.initializeIfLoaded("dynmap", "net.blay09.mods.waystones.compat.DynmapIntegration");
        Balm.initializeIfLoaded(Compat.UNBREAKABLES, "net.blay09.mods.waystones.compat.UnbreakablesIntegration");
    }
}
