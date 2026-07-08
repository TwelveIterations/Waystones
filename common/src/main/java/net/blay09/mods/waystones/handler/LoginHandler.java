package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.blay09.mods.waystones.network.message.ClientboundSetPreferencesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LoginHandler {

    public static void onPlayerLogin(PlayerLoginEvent event) {
        ServerPlayer player = event.getPlayer();
        Balm.networking().sendTo(player, new ClientboundSetPreferencesPacket(PlayerWaystoneManager.getWaystoneSortMode(player)));
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
