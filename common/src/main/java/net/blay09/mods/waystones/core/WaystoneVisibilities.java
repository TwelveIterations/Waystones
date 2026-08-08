package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class WaystoneVisibilities {
    public static List<WaystoneVisibility> getVisibilityOptions(ServerPlayer player, Waystone waystone) {
        final var result = new ArrayList<WaystoneVisibility>();
        final var defaultVisibility = WaystonesConfig.getActive().general.defaultVisibility.asWaystoneVisibility();
        result.add(defaultVisibility);
        final var baseVisibility = WaystoneVisibility.fromWaystoneType(waystone.getWaystoneType());
        if (!result.contains(baseVisibility)) {
            result.add(baseVisibility);
        }
        if (baseVisibility == WaystoneVisibility.ACTIVATION) {
            if (!result.contains(WaystoneVisibility.TEAM)) {
                result.add(WaystoneVisibility.TEAM);
            }
            if (WaystonePermissionManager.isAllowedVisibility(WaystoneVisibility.GLOBAL) || WaystonePermissionManager.skipsPermissions(player)) {
                if (!result.contains(WaystoneVisibility.GLOBAL)) {
                    result.add(WaystoneVisibility.GLOBAL);
                }
            }
        }
        return result.stream().filter(it -> it.isSupportedForWaystoneType(waystone.getWaystoneType())).toList();
    }
}
