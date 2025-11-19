package net.blay09.mods.waystones.handler;

public class ModEventHandlers {
    public static void initialize() {
        LoginHandler.register();
        WarpDamageResetHandler.register();
        WarpDamageResetHandler.register();
        WaystoneActivationStatHandler.register();
        WaystoneDebugHandler.register();
        WaystoneEditInteractionHandler.register();
    }
}
