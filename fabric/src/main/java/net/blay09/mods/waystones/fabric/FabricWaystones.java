package net.blay09.mods.waystones.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.waystones.Waystones;
import net.fabricmc.api.ModInitializer;

public class FabricWaystones implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(Waystones.MOD_ID, FabricLoadContext.INSTANCE, new Waystones());

        Balm.initializeIfLoaded("repurposed_structures", "net.blay09.mods.waystones.fabric.compat.RepurposedStructuresIntegration");
    }
}
