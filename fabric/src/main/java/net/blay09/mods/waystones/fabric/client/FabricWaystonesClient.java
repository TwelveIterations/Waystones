package net.blay09.mods.waystones.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricWaystonesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(Waystones.MOD_ID, FabricLoadContext.INSTANCE, new WaystonesClient());
    }
}
