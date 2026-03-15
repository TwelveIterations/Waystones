package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashSet;
import java.util.List;

public class WaystoneVisibilities {
    public static List<WaystoneVisibility> getVisibilityOptions(ServerPlayer player, Waystone waystone) {
        final var waystoneKind = waystone.getWaystoneKind();
        final var result = new LinkedHashSet<WaystoneVisibility>();
        result.add(WaystonesConfig.getActive().general.defaultVisibility);
        result.add(WaystoneVisibility.getDefaultForWaystoneKind(waystoneKind));
        if (WaystonePermissionManager.isAllowedVisibility(WaystoneVisibility.GLOBAL) || WaystonePermissionManager.skipsPermissions(player)) {
            result.add(WaystoneVisibility.GLOBAL);
        }
        return result.stream().filter(it -> it.isSupportedForWaystoneKind(waystoneKind)).toList();
    }
}
