package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.error.WaystoneEditError;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.permission.ModPermissions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.Set;

public class WaystonePermissionManager {

    private static final Set<WaystoneVisibility> DEFAULT_VISIBILITIES = Set.of(WaystoneVisibility.SHARD_ONLY, WaystoneVisibility.ACTIVATION, WaystoneVisibility.SHARESTONES);

    public static Optional<WaystoneEditError> mayEditWaystone(ServerPlayer player, Waystone waystone) {
        if (skipsPermissions(player)) {
            return Optional.empty();
        }

        final var config = WaystonesConfig.getActive();
        if (waystone.hasOwner() && config.general.restrictedWaystones.contains(WaystoneOrigin.PLAYER) && !waystone.isOwner(player)) {
            return Optional.of(new WaystoneEditError.NotOwner());
        }

        if (waystone.getOrigin() != WaystoneOrigin.PLAYER && config.general.restrictedWaystones.contains(waystone.getOrigin())) {
            return Optional.of(new WaystoneEditError.NotOwner());
        }

        if (!isAllowedVisibility(waystone.getVisibility())) {
            return Optional.of(new WaystoneEditError.RequiresCreative());
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
