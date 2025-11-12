package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.event.*;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;

public class ModEventHandlers {
    public static void initialize(BalmEvents events) {
        events.onEvent(PlayerLoginEvent.class, LoginHandler::onPlayerLogin);
        events.onEvent(LivingDamageEvent.class, WarpDamageResetHandler::onDamage);
        events.onEvent(WaystoneActivatedEvent.class, WaystoneActivationStatHandler::onWaystoneActivated);
        events.onEvent(UseBlockEvent.class, WaystoneDebugHandler::onWaystoneUsed);
        events.onEvent(UseBlockEvent.class, WaystoneEditInteractionHandler::onUseBlock);
    }
}
