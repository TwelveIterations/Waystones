package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.resources.Identifier;

public class LoginHandler {

    public static void register() {
        ServerPlayerCallback.Join.EVENT.register(player -> {
            WaystoneSyncManager.sendSortingIndex(player);
            WaystoneSyncManager.ensureDynamicGroups(player);
            WaystoneSyncManager.sendWaystoneGroups(player);
            WaystoneSyncManager.sendActivatedWaystones(player);
            WaystoneSyncManager.sendWaystonesOfType(WaystoneKinds.WARP_PLATE, player);
            for (Identifier dyedSharestone : WaystoneKinds.SHARESTONES) {
                WaystoneSyncManager.sendWaystonesOfType(dyedSharestone, player);
            }
        });
    }
}
