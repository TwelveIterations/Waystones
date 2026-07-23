package net.blay09.mods.waystones.core;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

import java.util.*;

public class WaystoneIndexManager {

    private static final SetMultimap<String, UUID> waystonesByTeamName = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private static final Set<UUID> globalWaystones = new LinkedHashSet<>();

    public static void rebuildIndex(MinecraftServer server) {
        waystonesByTeamName.clear();
        globalWaystones.clear();
        for (final var waystone : SavedDataWaystonesStore.get(server).getWaystones()) {
            add(server, waystone);
        }
    }

    public static void visibilityChanged(MinecraftServer server, Waystone waystone, WaystoneVisibility previousVisibility) {
        if (previousVisibility == WaystoneVisibility.TEAM || previousVisibility == WaystoneVisibility.GLOBAL) {
            remove(waystone);
        }
        add(server, waystone);
    }

    public static void waystoneRemoved(Waystone waystone) {
        remove(waystone);
    }

    public static void playerTeamChanged(MinecraftServer server, String playerName) {
        for (final var waystone : SavedDataWaystonesStore.get(server).getWaystones()) {
            if (PlayerWaystoneManager.getOwnerUsername(waystone, server).filter(playerName::equals).isPresent()) {
                remove(waystone);
                if (waystone.getVisibility() == WaystoneVisibility.TEAM) {
                    add(server, waystone);
                }
            }
        }
    }

    public static Collection<Waystone> getTargets(ServerPlayer player) {
        final var result = new ArrayList<Waystone>();
        result.addAll(getGlobalTargets(player));
        result.addAll(getTeamTargets(player));
        return result;
    }

    private static Collection<Waystone> getGlobalTargets(ServerPlayer player) {
        if (globalWaystones.isEmpty()) {
            return List.of();
        }

        final var store = SavedDataWaystonesStore.get(player.level().getServer());
        final var result = new ArrayList<Waystone>();
        for (final var waystoneId : globalWaystones) {
            store.getWaystoneById(waystoneId)
                    .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.GLOBAL)
                    .ifPresent(result::add);
        }
        return result;
    }

    private static Collection<Waystone> getTeamTargets(ServerPlayer player) {
        final var team = player.getTeam();
        if (team == null) {
            return List.of();
        }

        final var waystoneIds = waystonesByTeamName.get(team.getName());
        if (waystoneIds.isEmpty()) {
            return List.of();
        }

        final var store = SavedDataWaystonesStore.get(player.level().getServer());
        final var result = new ArrayList<Waystone>();
        for (final var waystoneId : waystoneIds) {
            store.getWaystoneById(waystoneId)
                    .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.TEAM)
                    .ifPresent(result::add);
        }
        return result;
    }

    private static void add(MinecraftServer server, Waystone waystone) {
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
            globalWaystones.add(waystone.getWaystoneUid());
            return;
        }

        if (waystone.getVisibility() != WaystoneVisibility.TEAM) {
            return;
        }

        PlayerWaystoneManager.getOwnerUsername(waystone, server)
                .map(ownerUsername -> server.getScoreboard().getPlayersTeam(ownerUsername))
                .map(PlayerTeam::getName)
                .ifPresent(teamName -> waystonesByTeamName.put(teamName, waystone.getWaystoneUid()));
    }

    private static void remove(Waystone waystone) {
        globalWaystones.remove(waystone.getWaystoneUid());
        waystonesByTeamName.values().remove(waystone.getWaystoneUid());
    }
}
