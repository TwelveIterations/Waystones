package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.network.message.*;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
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

        for (ServerPlayer player : PlayerWaystoneManager.getWaystoneAwareOnlinePlayers(server, waystone)) {
            sendWaystoneRemoval(player, waystone, wasDestroyed);
        }
    }

    public static void sendSortingIndex(Player player) {
        final var sortingIndex = PlayerWaystoneManager.getSortingIndex(player);
        Balm.networking().sendTo(player, new ClientboundSortingIndexPacket(sortingIndex));
    }

    public static void ensureDynamicGroups(Player player) {
        ensureDynamicGroupsFromWaystones(player, PlayerWaystoneManager.getActivatedWaystones(player));
        final var waystonesStore = SavedDataWaystonesStore.get(player.level().getServer());
        for (final var sharestone : WaystoneKinds.SHARESTONES) {
            ensureDynamicGroupsFromWaystones(player, waystonesStore.getWaystonesByKind(sharestone));
        }
    }

    public static void sendWaystoneGroups(Player player) {
        final var groupRegistry = PlayerWaystoneManager.getWaystoneGroupRegistry(player);
        Balm.networking().sendTo(player, new ClientboundWaystoneGroupsPacket(List.copyOf(groupRegistry)));
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
        Balm.networking().sendTo(player, new ClientboundKnownWaystonesPacket(WaystoneKinds.WAYSTONE, waystones));
    }

    public static void sendWaystonesOfType(Identifier waystoneType, ServerPlayer player) {
        final var waystones = PlayerWaystoneManager.getPlayerDecoratedWaystones(player, SavedDataWaystonesStore.get(player.level().getServer()).getWaystonesByKind(waystoneType));
        ensureDynamicGroupsFromWaystones(player, waystones);
        sendWaystoneGroups(player);
        Balm.networking().sendTo(player, new ClientboundKnownWaystonesPacket(waystoneType, waystones));
    }

    public static void sendWaystoneUpdate(Player player, Waystone waystone) {
        // If this is a waystone, only send an update if the player has activated it already
        if (!waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
            PlayerWaystoneManager.ensureWaystoneGroups(player, waystone);
            sendWaystoneGroups(player);
            final var decoratedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(player, waystone);
            Balm.networking().sendTo(player, new ClientboundUpdateWaystonePacket(decoratedWaystone));
        }
    }

    public static void sendWaystoneRemoval(Player player, Waystone waystone, boolean wasDestroyed) {
        Balm.networking().sendTo(player, new ClientboundWaystoneRemovedPacket(waystone.getWaystoneKind(), waystone.getWaystoneUid(), wasDestroyed));
    }

}
