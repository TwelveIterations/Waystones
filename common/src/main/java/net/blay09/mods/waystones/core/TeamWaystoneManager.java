package net.blay09.mods.waystones.core;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

import java.util.*;

public class TeamWaystoneManager {

    private static final SetMultimap<String, UUID> waystonesByTeamName = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    public static void rebuildIndex(MinecraftServer server) {
        waystonesByTeamName.clear();
        for (final var waystone : WaystoneManagerImpl.get(server).getWaystones().toList()) {
            if (waystone.getVisibility() == WaystoneVisibility.TEAM) {
                add(server, waystone);
            }
        }
    }

    public static void visibilityChanged(MinecraftServer server, Waystone waystone, WaystoneVisibility previousVisibility) {
        if (previousVisibility == WaystoneVisibility.TEAM) {
            remove(waystone);
        }
        if (waystone.getVisibility() == WaystoneVisibility.TEAM) {
            add(server, waystone);
        }
    }

    public static void playerTeamChanged(MinecraftServer server, String playerName) {
        for (final var waystone : WaystoneManagerImpl.get(server).getWaystones().toList()) {
            if (PlayerWaystoneManager.getOwnerUsername(waystone, server).filter(playerName::equals).isPresent()) {
                remove(waystone);
                if (waystone.getVisibility() == WaystoneVisibility.TEAM) {
                    add(server, waystone);
                }
            }
        }
    }

    public static Collection<Waystone> getTargets(ServerPlayer player) {
        final var team = player.getTeam();
        if (team == null) {
            return List.of();
        }

        final var waystoneIds = waystonesByTeamName.get(team.getName());
        if (waystoneIds.isEmpty()) {
            return List.of();
        }

        final var store = WaystoneManagerImpl.get(player.level().getServer());
        final var result = new ArrayList<Waystone>();
        for (final var waystoneId : waystoneIds) {
            store.getWaystoneById(waystoneId)
                    .filter(waystone -> waystone.getVisibility() == WaystoneVisibility.TEAM)
                    .ifPresent(result::add);
        }
        return result;
    }

    private static void add(MinecraftServer server, Waystone waystone) {
        PlayerWaystoneManager.getOwnerUsername(waystone, server)
                .map(ownerUsername -> server.getScoreboard().getPlayersTeam(ownerUsername))
                .map(PlayerTeam::getName)
                .ifPresent(teamName -> waystonesByTeamName.put(teamName, waystone.getWaystoneUid()));
    }

    private static void remove(Waystone waystone) {
        waystonesByTeamName.values().remove(waystone.getWaystoneUid());
    }
}
