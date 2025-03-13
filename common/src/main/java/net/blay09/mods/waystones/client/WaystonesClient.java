package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.InternalClientMethodsImpl;
import net.blay09.mods.waystones.api.client.WaystonesClientAPI;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.store.EventfulWaystonesStore;
import net.blay09.mods.waystones.store.InMemoryWaystonesStore;
import net.blay09.mods.waystones.store.WaystonesStore;
import net.minecraft.client.ClientBrandRetriever;

import java.util.List;
import java.util.Locale;

public class WaystonesClient {
    private static final WaystonesStore waystonesStore = new EventfulWaystonesStore(new InMemoryWaystonesStore(List.of()));

    public static void initialize() {
        WaystonesClientAPI.__internalMethods = new InternalClientMethodsImpl();
        RequirementClientRegistry.registerDefaults();

        ModClientEventHandlers.initialize();
        ModRenderers.initialize(BalmClient.getRenderers());
        ModScreens.initialize(BalmClient.getScreens());
        ModModels.initialize(BalmClient.getModels());

        InventoryButtonGuiHandler.initialize();

        Compat.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains(Compat.VIVECRAFT);
    }

    public static WaystonesStore getWaystonesStore() {
        return waystonesStore;
    }
}
