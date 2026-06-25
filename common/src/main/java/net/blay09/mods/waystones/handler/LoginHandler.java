package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.resources.Identifier;

import java.util.List;

public class LoginHandler {

    public static void register() {
        ServerPlayerCallback.Join.EVENT.register(player -> {
            // Introduce all global waystones to this player
            List<Waystone> globalWaystones = SavedDataWaystonesStore.get(player.level().getServer()).getGlobalWaystones();
            for (Waystone waystone : globalWaystones) {
                if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                    PlayerWaystoneManager.activateWaystone(player, waystone);
                }
            }

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
