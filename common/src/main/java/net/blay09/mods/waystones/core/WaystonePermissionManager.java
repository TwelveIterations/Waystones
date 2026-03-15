package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.config.rules.WaystoneRuleContext;
import net.blay09.mods.waystones.permission.ModPermissions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.Set;

public class WaystonePermissionManager {

    private static final Set<WaystoneVisibility> DEFAULT_VISIBILITIES = Set.of(WaystoneVisibility.SHARD_ONLY, WaystoneVisibility.ACTIVATION, WaystoneVisibility.SHARESTONES);

    public static Optional<Component> mayEditWaystone(ServerPlayer player, Waystone waystone) {
        if (skipsPermissions(player)) {
            return Optional.empty();
        }

        final var context = MutableShogiContext.of(player);
        WaystoneRuleContext.setEffectiveWaystone(context, waystone);
        final var editResult = WaystonesRules.mayEdit.get(context);
        if (editResult.right().isPresent()) {
            return editResult.right().map(Coercion.COMPONENT);
        } else if (editResult.left().orElse(false)) {
            return Optional.of(Component.translatable("chat.waystones.not_allowed_to_edit"));
        }

        if (!isAllowedVisibility(waystone.getVisibility())) {
            return Optional.of(Component.translatable("chat.waystones.only_creative_can_edit"));
        }

        return Optional.empty();
    }

    public static boolean isAllowedVisibility(WaystoneVisibility visibility) {
        final var config = WaystonesConfig.getActive();
        return DEFAULT_VISIBILITIES.contains(visibility) || config.general.allowedVisibilities.contains(visibility) || config.general.defaultVisibility == visibility;
    }

    public static boolean skipsPermissions(ServerPlayer player) {
        return Balm.permissions().hasPermission(player, ModPermissions.EDIT_ALL);
    }

    public static boolean isEntityDeniedTeleports(Entity entity) {
        final var deniedEntities = WaystonesConfig.getActive().teleports.entityDenyList;
        return deniedEntities.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }
}
