package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LoginHandler {

    public static void onPlayerLogin(PlayerLoginEvent event) {
        ServerPlayer player = event.getPlayer();
        WaystoneSyncManager.sendSortingIndex(player);
        WaystoneSyncManager.ensureDefaultGroups(player);
        WaystoneSyncManager.ensureDynamicGroups(player);
        WaystoneSyncManager.sendWaystoneGroups(player);
        WaystoneSyncManager.sendActivatedWaystones(player);
        WaystoneSyncManager.sendWaystonesOfType(WaystoneTypes.WARP_PLATE, player);
        for (ResourceLocation dyedSharestone : WaystoneTypes.SHARESTONES) {
            WaystoneSyncManager.sendWaystonesOfType(dyedSharestone, player);
        }
        WaystoneSyncManager.sendWaystoneCooldowns(player);
    }

}
