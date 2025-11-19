package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.platform.event.callback.RenderCallback;
import net.blay09.mods.waystones.handler.WarpStoneFOVHandler;

public class ModClientEventHandlers {
    public static void initialize() {
        RenderCallback.UpdateFov.EVENT.register(WarpStoneFOVHandler::onFOV);
    }
}
