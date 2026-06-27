package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.gui.screens.inventory.BalmMenuScreenRegistrar;
import net.blay09.mods.balm.client.platform.config.BalmConfigScreenRegistrar;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.client.config.WaystonesConfigScreenFactory;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.store.EventfulWaystonesStore;
import net.blay09.mods.waystones.store.InMemoryWaystonesStore;
import net.blay09.mods.waystones.store.WaystonesStore;
import net.minecraft.resources.Identifier;

import java.util.List;

public class WaystonesClient implements BalmClientModule {
    private static final WaystonesStore waystonesStore = new EventfulWaystonesStore(new InMemoryWaystonesStore(List.of()));

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "client");
    }

    @Override
    public void registerBlockStateModels(BalmBlockStateModelRegistrar models) {
        ModRenderers.initialize(models);
    }

    @Override
    public void registerBlockColors(BalmBlockColorRegistrar blockColors) {
        ModRenderers.initialize(blockColors);
    }

    @Override
    public void registerBlockEntityRenderers(BalmBlockEntityRendererRegistrar blockEntityRenderers) {
        ModRenderers.initialize(blockEntityRenderers);
    }

    @Override
    public void registerMenuScreens(BalmMenuScreenRegistrar screens) {
        ModScreens.initialize(screens);
    }

    @Override
    public void registerConfigScreen(BalmConfigScreenRegistrar configScreens) {
        configScreens.register(WaystonesConfigScreenFactory::create);
    }

    @Override
    public void initialize() {
        ModClientEventHandlers.initialize();
        InventoryButtonGuiHandler.initialize();

        RequirementClientRegistry.registerDefaults();
    }

    public static WaystonesStore getWaystonesStore() {
        return waystonesStore;
    }
}
