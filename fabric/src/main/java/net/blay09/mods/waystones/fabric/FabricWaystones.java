package net.blay09.mods.waystones.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.waystones.Waystones;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class FabricWaystones implements ModInitializer {

    private static final Logger logger = LoggerFactory.getLogger(FabricWaystones.class);

    @Override
    public void onInitialize() {
        Balm.initialize(Waystones.MOD_ID, EmptyLoadContext.INSTANCE, Waystones::initialize);

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
