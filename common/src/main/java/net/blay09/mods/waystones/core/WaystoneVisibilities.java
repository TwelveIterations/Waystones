package net.blay09.mods.waystones.core;

import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.config.rules.WaystoneRuleContext;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashSet;
import java.util.List;

public class WaystoneVisibilities {
    public static List<WaystoneVisibility> getVisibilityOptions(ServerPlayer player, Waystone waystone) {
        final var waystoneKind = waystone.getWaystoneKind();
        final var result = new LinkedHashSet<WaystoneVisibility>();
        result.add(WaystonesConfig.getActive().rules.defaultVisibility.getVisibility());
        result.add(WaystoneVisibility.getDefaultForWaystoneKind(waystoneKind));
        result.add(WaystoneVisibility.TEAM);
        final var context = MutableShogiContext.of(player);
        WaystoneRuleContext.setEffectiveWaystone(context, waystone);
        final var mayManageGlobalWaystones = WaystonesRules.mayManageGlobalWaystones.getOrElse(context, false);
        if (mayManageGlobalWaystones) {
            result.add(WaystoneVisibility.GLOBAL);
        }
        return result.stream().filter(it -> it.isSupportedForWaystoneKind(waystoneKind)).toList();
    }
}
