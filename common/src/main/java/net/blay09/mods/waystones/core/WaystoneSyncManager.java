package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

    public static void ensureDefaultGroups(Player player) {
        PlayerWaystoneManager.ensureWaystoneGroups(player, List.of(
                WaystoneGroups.FAVORITES,
                WaystoneGroups.PLAYERS,
                WaystoneGroups.GLOBAL,
                WaystoneGroups.TEAM));
    }

    public static void ensureDynamicGroups(ServerPlayer player) {
        ensureDynamicGroupsFromWaystones(player, PlayerWaystoneManager.getTargetsForPlayer(player));
        final var waystoneManager = WaystoneManagerImpl.get(player.level().getServer());
        for (final var sharestone : WaystoneTypes.SHARESTONES) {
            ensureDynamicGroupsFromWaystones(player, waystoneManager.getWaystonesByType(sharestone).collect(Collectors.toList()));
        }
    }

    public static void sendWaystoneGroups(Player player) {
        final var groupRegistry = PlayerWaystoneManager.getWaystoneGroupRegistry(player);
        Balm.getNetworking().sendTo(player, new ClientboundWaystoneGroupsPacket(List.copyOf(groupRegistry)));
    }

    private static void ensureDynamicGroupsFromWaystones(Player player, Collection<? extends Waystone> waystones) {
        for (final var waystone : waystones) {
            PlayerWaystoneManager.ensureWaystoneGroups(player, WaystoneGroups.getDynamicGroupDefinitions(waystone));
        }
    }

    public static void sendActivatedWaystones(Player player) {
        ensureDynamicGroupsFromWaystones(player, PlayerWaystoneManager.getActivatedWaystones(player));
        sendWaystoneGroups(player);
        final var waystones = PlayerWaystoneManager.getPlayerDecoratedWaystones(player, PlayerWaystoneManager.getActivatedWaystones(player));
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(WaystoneTypes.WAYSTONE, waystones));
    }

    public static void sendWaystonesOfType(ResourceLocation waystoneType, ServerPlayer player) {
        final var warpPlates = PlayerWaystoneManager.getPlayerDecoratedWaystones(player, WaystoneManagerImpl.get(player.server).getWaystonesByType(waystoneType).collect(Collectors.toList()));
        ensureDynamicGroupsFromWaystones(player, warpPlates);
        sendWaystoneGroups(player);
        Balm.getNetworking().sendTo(player, new KnownWaystonesMessage(waystoneType, warpPlates));
    }

    public static void sendWaystoneUpdate(Player player, Waystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            PlayerWaystoneManager.ensureWaystoneGroups(player, waystone);
            sendWaystoneGroups(player);
            final var decoratedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, waystone);
            Balm.getNetworking().sendTo(player, new UpdateWaystoneMessage(decoratedWaystone));
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
}
