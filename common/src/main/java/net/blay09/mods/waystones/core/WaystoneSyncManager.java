package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.network.message.KnownWaystonesMessage;
import net.blay09.mods.waystones.network.message.PlayerWaystoneCooldownsMessage;
import net.blay09.mods.waystones.network.message.UpdateWaystoneMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSyncManager {

    public static void sendWaystoneUpdateToAll(@Nullable MinecraftServer server, IWaystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendWaystoneUpdate(player, waystone);
            sendActivatedWaystones(player);
        }
    }

    public static void sendActivatedWaystones(Player player) {
        List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(WaystoneTypes.WAYSTONE, waystones));
    }

    public static void sendWarpPlates(ServerPlayer player) {
        List<IWaystone> warpPlates = WaystoneManager.get(player.server).getWaystonesByType(WaystoneTypes.WARP_PLATE).collect(Collectors.toList());
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(WaystoneTypes.WARP_PLATE, warpPlates));
    }

    public static void sendWaystoneUpdate(Player player, IWaystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new UpdateWaystoneMessage(waystone));
        }
    }

    public static void sendWaystoneCooldowns(Player player) {
        long inventoryButtonCooldownUntil = PlayerWaystoneManager.getInventoryButtonCooldownUntil(player);
        long warpStoneCooldownUntil = PlayerWaystoneManager.getWarpStoneCooldownUntil(player);
        Balm.getNetworking().sendTo(player, new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil));
    }
}
