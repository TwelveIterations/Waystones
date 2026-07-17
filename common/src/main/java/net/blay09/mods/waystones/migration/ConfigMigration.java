package net.blay09.mods.waystones.migration;

import net.blay09.mods.waystones.config.WaystonesConfigData;

import java.util.List;

public class ConfigMigration {

    private static final List<List<String>> LEGACY_WARP_REQUIREMENTS_DEFAULTS = List.of(
            List.of(
                    "[is_not_interdimensional] scaled_add_xp_cost(distance, 0.01)",
                    "[is_interdimensional] add_xp_cost(27)",
                    "[source_is_warp_plate] multiply_xp_cost(0)",
                    "[target_is_global] multiply_xp_cost(0)",
                    "min_xp_cost(0)",
                    "max_xp_cost(27)",
                    "[source_is_inventory_button] add_cooldown(inventory_button, 300)"),
            List.of(
                    "[is_not_interdimensional] scaled_add_xp_cost(distance, 0.01)",
                    "[is_interdimensional] add_xp_cost(27)",
                    "[source_is_warp_plate] multiply_xp_cost(0)",
                    "[source_is_warp_stone] add_durability_cost(80)",
                    "[target_is_global] multiply_xp_cost(0)",
                    "min_xp_cost(0)",
                    "max_xp_cost(27)",
                    "[source_is_inventory_button] add_cooldown(inventory_button, 300)"),
            List.of(
                    "[is_not_interdimensional] scaled_add_xp(distance, 0.01)",
                    "[is_interdimensional] add_xp(27)",
                    "[source_is_warp_plate] multiply_xp(0)",
                    "[target_is_global] multiply_xp(0)",
                    "min_xp(0)",
                    "max_xp(27)",
                    "[source_is_inventory_button] add_cooldown(inventory_button, 300)"),
            List.of(
                    "[is_not_interdimensional] scaled_add_xp(distance, 0.01)",
                    "[is_interdimensional] add_xp(27)",
                    "[source_is_warp_plate] multiply_xp(0)",
                    "[target_is_global] multiply_xp(0)",
                    "min_xp(0)",
                    "max_xp(27)")
    );

    public static boolean migrateWarpRequirementsDefault(WaystonesConfigData config) {
        if (needsWarpRequirementsDefaultMigration(config)) {
            config.teleports.warpRequirements = WaystonesConfigData.DEFAULT_WARP_REQUIREMENTS;
            return true;
        }

        return false;
    }

    public static boolean needsWarpRequirementsDefaultMigration(WaystonesConfigData config) {
        return LEGACY_WARP_REQUIREMENTS_DEFAULTS.contains(config.teleports.warpRequirements);
    }
}
