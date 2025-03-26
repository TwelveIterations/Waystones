package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.waystones.compat.Compat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

@Mod(Waystones.MOD_ID)
public class NeoForgeWaystones {

    private static final Logger logger = LoggerFactory.getLogger(NeoForgeWaystones.class);

    public NeoForgeWaystones(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initialize(Waystones.MOD_ID, context, Waystones::initialize);

        Balm.initializeIfLoaded(Compat.THEONEPROBE, "net.blay09.mods.waystones.compat.TheOneProbeIntegration");

        // TODO would be nice if we could use Balm.initializeIfLoaded here, but it might run too late at the moment)
        if (Balm.isModLoaded("repurposed_structures")) {
            try {
                Class.forName("net.blay09.mods.waystones.compat.RepurposedStructuresIntegration").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException e) {
                logger.error("Failed to load Repurposed Structures integration", e);
            }
        }
    }
}
