package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.command.ModCommands;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.requirement.RequirementRegistry;
import net.blay09.mods.waystones.handler.ModEventHandlers;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.network.ModNetworking;
import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.worldgen.ModWorldGen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waystones {

    public static final Logger logger = LoggerFactory.getLogger(Waystones.class);

    public static final String MOD_ID = "waystones";

    public static void initialize() {
        WaystonesAPI.__internalMethods = new InternalMethodsImpl();
        RequirementRegistry.registerDefaults();

        WaystonesConfig.initialize();
        Balm.getResources().registerResourceCondition(id("force_spawn_in_villages"), ForceSpawnInVillagesCondition.CODEC);
        ModStats.initialize();
        ModEventHandlers.initialize();
        ModNetworking.initialize(Balm.getNetworking());
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize();
        ModItems.initialize(Balm.getItems());
        ModMenus.initialize();
        ModWorldGen.initialize(Balm.getWorldGen());
        ModCommands.initialize(Balm.getCommands());
        ModComponents.initialize(Balm.getComponents());

        if (WaystonesConfig.getActive().compatibility.blueMap) {
            Balm.initializeIfLoaded("bluemap", "net.blay09.mods.waystones.compat.BlueMapIntegration");
        }

        if (WaystonesConfig.getActive().compatibility.dynmap) {
            Balm.initializeIfLoaded("dynmap", "net.blay09.mods.waystones.compat.DynmapIntegration");
        }

        Balm.initializeIfLoaded(Compat.UNBREAKABLES, "net.blay09.mods.waystones.compat.UnbreakablesIntegration");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
