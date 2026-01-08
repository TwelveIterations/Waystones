package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WaystoneSyncManager {

    public static void sendWaystoneUpdateToAll(@Nullable MinecraftServer server, Waystone waystone) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendWaystoneUpdate(player, waystone);
            sendActivatedWaystones(player);
        }
    }

    public static void sendWaystoneRemovalToAll(@Nullable MinecraftServer server, Waystone waystone, boolean wasDestroyed) {
        if (server == null) {
            return;
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            sendWaystoneRemoval(player, waystone, wasDestroyed);
        }
    }

    public static void sendSortingIndex(Player player) {
        final var sortingIndex = PlayerWaystoneManager.getSortingIndex(player);
        Balm.getNetworking().sendTo(player, new SortingIndexMessage(sortingIndex));
    }

    public static void sendActivatedWaystones(Player player) {
        final var waystones = PlayerWaystoneManager.getActivatedWaystones(player);
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(WaystoneTypes.WAYSTONE, waystones));
    }

    public static void sendWaystonesOfType(ResourceLocation waystoneType, ServerPlayer player) {
        if (WaystoneTypes.isSharestone(waystoneType)) {
            sendSharestonesOfType(waystoneType, player);
            return;
        }
        List<Waystone> waystones = WaystoneManagerImpl.get(player.server).getWaystonesByType(waystoneType).collect(Collectors.toList());
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(waystoneType, waystones));
    }

    public static void sendSharestonesOfType(ResourceLocation waystoneType, ServerPlayer player) {
        List<Waystone> waystones = WaystoneManagerImpl.get(player.server).getWaystonesByType(waystoneType).collect(Collectors.toList());
        if (!WaystonesConfig.getActive().compatibility.sharestonesSendCoordsToClients) {
            Balm.getNetworking().sendTo(player, new RestrictedWaystonesMessage(waystoneType, buildSharestoneSyncData(player, waystones)));
            return;
        }
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(waystoneType, waystones));
    }

    public static void sendWaystoneUpdate(ServerPlayer player, Waystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType()) && !WaystonesConfig.getActive().compatibility.sharestonesSendCoordsToClients) {
                Balm.getNetworking().sendTo(player, new UpdateRestrictedWaystoneMessage(SharestoneSyncData.fromWaystone(player, waystone)));
                return;
            }
            Balm.getNetworking().sendTo(player, new UpdateWaystoneMessage(waystone));
        }
    }

    public static void sendWaystoneRemoval(Player player, Waystone waystone, boolean wasDestroyed) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new WaystoneRemovedMessage(waystone.getWaystoneType(), waystone.getWaystoneUid(), wasDestroyed));
        }
    }

    public static void sendWaystoneCooldowns(Player player) {
        final var cooldowns = PlayerWaystoneManager.getCooldowns(player);
        Balm.getNetworking().sendTo(player, new PlayerWaystoneCooldownsMessage(cooldowns));
    }

    private static List<SharestoneSyncData> buildSharestoneSyncData(ServerPlayer player, List<Waystone> waystones) {
        return waystones.stream()
                .map(waystone -> SharestoneSyncData.fromWaystone(player, waystone))
                .collect(Collectors.toList());
    }
}
