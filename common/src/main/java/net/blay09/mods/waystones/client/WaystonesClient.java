package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.waystones.InternalClientMethodsImpl;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.client.WaystonesClientAPI;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.store.EventfulWaystonesStore;
import net.blay09.mods.waystones.store.InMemoryWaystonesStore;
import net.blay09.mods.waystones.store.WaystonesStore;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;

public class WaystonesClient implements BalmClientModule {
    private static final WaystonesStore waystonesStore = new EventfulWaystonesStore(new InMemoryWaystonesStore(List.of()));

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "client");
    }

    @Override
    public void registerEvents(BalmEvents events) {
        ModClientEventHandlers.initialize(events);
        InventoryButtonGuiHandler.initialize(events);
    }

    @Override
    public void registerRenderers(BalmRenderers renderers) {
        ModRenderers.initialize(renderers);
    }

    @Override
    public void registerScreens(BalmScreens screens) {
        ModScreens.initialize(screens);
    }

    @Override
    public void registerModels(BalmModels models) {
        ModModels.initialize(models);
    }

    @Override
    public void initialize() {
        WaystonesClientAPI.__internalMethods = new InternalClientMethodsImpl();
        RequirementClientRegistry.registerDefaults();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);
    }

    public static WaystonesStore getWaystonesStore() {
        return waystonesStore;
    }
}
