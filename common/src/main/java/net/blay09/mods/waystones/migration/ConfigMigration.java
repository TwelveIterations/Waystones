package net.blay09.mods.waystones.migration;

import net.blay09.mods.waystones.config.WaystonesConfig;

import java.util.List;

public class ConfigMigration {

    private static final List<String> LEGACY_WARP_REQUIREMENTS_DEFAULT = List.of(
            "[is_not_interdimensional] scaled_add_xp_cost(distance, 0.01)",
            "[is_interdimensional] add_xp_cost(27)",
            "[source_is_warp_plate] multiply_xp_cost(0)",
            "[source_is_warp_stone] add_durability_cost(80)",
            "[target_is_global] multiply_xp_cost(0)",
            "min_xp_cost(0)",
            "max_xp_cost(27)",
            "[source_is_inventory_button] add_cooldown(inventory_button, 300)");

    private static final List<String> INITIAL_SHOGI_WARP_REQUIREMENTS_DEFAULT = List.of(
            "$xp_points_cost = if(condition = is_interdimensional, then = 27, else = $distance * 0.01)",
            "source_is_warp_plate, target_is_global -> $xp_points_cost = 0",
            "$xp_points_cost = clamp($xp_points_cost, 0, 27)",
            "source_is_warp_stone -> damage_item(80)",
            "source_is_inventory_button -> cooldown_cost('inventory_button', '300s')");

    private static final List<String> CONTEXTUAL_SHOGI_WARP_REQUIREMENTS_DEFAULT = List.of(
            "$xp_points_cost = if(condition = is_interdimensional, then = 27, else = $distance * 0.01)",
            "source(is_warp_plate()), target(is_global()) -> $xp_points_cost = 0",
            "$xp_points_cost = clamp($xp_points_cost, 0, 27)",
            "is_warp_stone -> damage_item(80)",
            "is_inventory_button -> cooldown_cost('inventory_button', '300s')");

    private static final List<String> SIMPLE_WARP_REQUIREMENTS_DEFAULT = List.of(
            "$xp_points_cost = $distance * 0.01",
            "is_interdimensional -> $xp_points_cost = 27",
            "source(is_warp_plate()) -> $xp_points_cost = 0",
            "target(is_global()) -> $xp_points_cost = 0",
            "$xp_points_cost = clamp($xp_points_cost, 0, 27)",
            "is_warp_stone -> damage_item(80)",
            "is_inventory_button -> cooldown_cost('inventory_button', '300s')");

    private static final List<String> PRE_WARP_SETTINGS_DEFAULT = List.of(
            "$uses_xp #= any(source(is_waystone), source(is_warp_stone))",
            "$uses_xp -> $xp_points_cost = $distance * 0.01",
            "$uses_xp + is_interdimensional -> $xp_points_cost = 27",
            "$uses_xp -> $xp_points_cost = clamp($xp_points_cost, 0, 27)",
            "is_warp_stone -> damage_item(80)",
            "is_inventory_button -> cooldown_cost('inventory_button', '300s')");

    private static final List<String> PRE_FLEETING_MEMORIAL_WARP_REQUIREMENTS_DEFAULT = List.of(
            "$uses_xp #= any(source(is_waystone), source(is_warp_stone))",
            "$uses_xp -> $xp_points_cost = $distance * $xp_per_block",
            "$uses_xp + is_interdimensional -> $xp_points_cost = $interdimensional_xp_cost",
            "$uses_xp -> $xp_points_cost = clamp($xp_points_cost, 0, $max_xp_cost)",
            "is_warp_stone -> damage_item(80)",
            "is_inventory_button -> cooldown_cost('inventory_button', $inventory_button_cooldown)");

    public static boolean migrateWarpRequirementsDefault(WaystonesConfig config) {
        if (needsWarpRequirementsDefaultMigration(config)) {
            config.rules.warpRequirements = WaystonesConfig.DEFAULT_WARP_REQUIREMENTS;
            return true;
        }

        return false;
    }

    public static boolean needsWarpRequirementsDefaultMigration(WaystonesConfig config) {
        return LEGACY_WARP_REQUIREMENTS_DEFAULT.equals(config.rules.warpRequirements)
                || INITIAL_SHOGI_WARP_REQUIREMENTS_DEFAULT.equals(config.rules.warpRequirements)
                || CONTEXTUAL_SHOGI_WARP_REQUIREMENTS_DEFAULT.equals(config.rules.warpRequirements)
                || SIMPLE_WARP_REQUIREMENTS_DEFAULT.equals(config.rules.warpRequirements)
                || PRE_WARP_SETTINGS_DEFAULT.equals(config.rules.warpRequirements)
                || PRE_FLEETING_MEMORIAL_WARP_REQUIREMENTS_DEFAULT.equals(config.rules.warpRequirements);
    }
}
