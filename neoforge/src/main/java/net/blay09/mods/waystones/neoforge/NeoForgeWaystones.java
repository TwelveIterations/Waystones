package net.blay09.mods.waystones.neoforge;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.waystones.Waystones;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Waystones.MOD_ID)
public class NeoForgeWaystones {
    public NeoForgeWaystones(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(Waystones.MOD_ID, context, new Waystones());

        Balm.initializeIfLoaded("repurposed_structures", "net.blay09.mods.waystones.neoforge.compat.RepurposedStructuresIntegration");
    }
}
