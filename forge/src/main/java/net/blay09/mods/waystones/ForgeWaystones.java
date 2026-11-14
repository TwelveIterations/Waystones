package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.blay09.mods.waystones.compat.Compat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

@Mod(Waystones.MOD_ID)
public class ForgeWaystones {

    private static final Logger logger = LoggerFactory.getLogger(ForgeWaystones.class);

    public ForgeWaystones() {
        Balm.initialize(Waystones.MOD_ID, EmptyLoadContext.INSTANCE, Waystones::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initializeMod(Waystones.MOD_ID, EmptyLoadContext.INSTANCE, WaystonesClient::initialize));

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
