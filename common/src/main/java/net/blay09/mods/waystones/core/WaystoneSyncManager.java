package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.network.message.*;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        Balm.getNetworking().sendTo(player, new ClientboundSortingIndexPacket(sortingIndex));
    }

    public static void sendActivatedWaystones(Player player) {
        final var waystones = new ArrayList<>(PlayerWaystoneManager.getActivatedWaystones(player));
        Balm.getNetworking().sendTo(player, new ClientboundKnownWaystonesPacket(WaystoneTypes.WAYSTONE, waystones));
    }

    public static void sendWaystonesOfType(ResourceLocation waystoneType, ServerPlayer player) {
        List<Waystone> warpPlates = new ArrayList<>(SavedDataWaystonesStore.get(player.server).getWaystonesByType(waystoneType));
        Balm.getNetworking().sendTo(player, new ClientboundKnownWaystonesPacket(waystoneType, warpPlates));
    }

    public static void sendWaystoneUpdate(Player player, Waystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new ClientboundUpdateWaystonePacket(waystone));
        }
    }

    public static void sendWaystoneRemoval(Player player, Waystone waystone, boolean wasDestroyed) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            Balm.getNetworking().sendTo(player, new ClientboundWaystoneRemovedPacket(waystone.getWaystoneType(), waystone.getWaystoneUid(), wasDestroyed));
        }
    }

    public static void sendWaystoneCooldowns(Player player) {
        final var cooldowns = PlayerWaystoneManager.getCooldowns(player);
        Balm.getNetworking().sendTo(player, new ClientboundPlayerWaystoneCooldownsPacket(cooldowns));
    }
}
