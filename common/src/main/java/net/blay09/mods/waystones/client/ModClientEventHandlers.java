package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.waystones.handler.WarpStoneFOVHandler;

public class ModClientEventHandlers {
    public static void initialize(BalmEvents events) {
        events.onEvent(FovUpdateEvent.class, WarpStoneFOVHandler::onFOV);
    }
}
