package net.blay09.mods.waystones;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.forge.platform.runtime.ForgeLoadContext;
import net.blay09.mods.waystones.client.ForgeWaystonesClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Waystones.MOD_ID)
public class ForgeWaystones {
    public ForgeWaystones(FMLJavaModLoadingContext context) {
        final var loadContext = new ForgeLoadContext(context.getModBusGroup());
        Balm.initializeMod(Waystones.MOD_ID, loadContext, new Waystones());
        if (FMLEnvironment.dist.isClient()) {
            BalmClient.initializeMod(Waystones.MOD_ID, loadContext, ForgeWaystonesClient::initialize);
        }

        Balm.initializeIfLoaded("repurposed_structures", "net.blay09.mods.waystones.compat.RepurposedStructuresIntegration");
    }
}
