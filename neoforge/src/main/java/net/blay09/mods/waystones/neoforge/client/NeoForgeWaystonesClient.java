package net.blay09.mods.waystones.neoforge.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Waystones.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeWaystonesClient {

    public NeoForgeWaystonesClient(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        BalmClient.initializeMod(Waystones.MOD_ID, context, new WaystonesClient());
    }
}
