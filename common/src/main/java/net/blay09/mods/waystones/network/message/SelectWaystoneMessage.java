package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SelectWaystoneMessage {

    private final UUID waystoneUid;

    public SelectWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(SelectWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.waystoneUid);
    }

    public static SelectWaystoneMessage decode(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        return new SelectWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, SelectWaystoneMessage message) {
        if (!(player.containerMenu instanceof WaystoneSelectionMenu)) {
            return;
        }

        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        WaystoneSelectionMenu container = (WaystoneSelectionMenu) player.containerMenu;
        PlayerWaystoneManager.tryTeleportToWaystone(player, waystone, container.getWarpMode(), container.getWaystoneFrom());
        player.closeContainer();
    }


}
