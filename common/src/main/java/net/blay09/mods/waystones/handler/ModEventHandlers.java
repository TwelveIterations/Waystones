package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;

public class ModEventHandlers {
    public static void initialize() {
        LoginHandler.register();
        WarpDamageResetHandler.register();
        WarpDamageResetHandler.register();
        WaystoneActivatedEvent.EVENT.register(WaystoneActivationStatHandler::onWaystoneActivated);
        WaystoneDebugHandler.register();
        WaystoneEditInteractionHandler.register();
    }
}
