package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.platform.event.callback.ServerLifecycleCallback;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.event.WaystoneTeleportEvent;
import net.blay09.mods.waystones.core.FleetingMemorialManager;
import net.blay09.mods.waystones.core.WaystoneIndexManager;

public class ModEventHandlers {
    public static void initialize() {
        LoginHandler.register();
        WarpDamageResetHandler.register();
        WarpDamageResetHandler.register();
        ServerLifecycleCallback.Started.EVENT.register(WaystoneIndexManager::rebuildIndex);
        EpitaphDeathHandler.register();
        WaystoneActivatedEvent.EVENT.register(WaystoneActivationStatHandler::onWaystoneActivated);
        WaystoneTeleportEvent.After.EVENT.register(FleetingMemorialManager::handleTeleportAfter);
        WaystoneDebugHandler.register();
        WaystoneEditInteractionHandler.register();
    }
}
