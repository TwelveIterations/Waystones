package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.requirement.CooldownRequirement;
import net.blay09.mods.waystones.requirement.ExperiencePointsRequirement;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class DefaultWarpRequirements {
    private static final String INVENTORY_COOLDOWN = "WaystonesInventoryButtonCooldown";
    private static final int INVENTORY_COOLDOWN_TICKS = 300 * 20;

    public static Either<List<?>, List<?>> resolve(WaystoneTeleportContext context) {
        if (!(context.getEntity() instanceof Player player)) {
            return Either.left(List.of());
        }

        final var requirements = new ArrayList<>();
        final var missing = new ArrayList<>();
        resolveExperienceRequirement(context, player, requirements, missing);
        resolveInventoryCooldownRequirement(context, player, requirements, missing);
        applyDurabilityCost(context, player);
        return player.getAbilities().instabuild || missing.isEmpty() ? Either.left(requirements) : Either.right(missing);
    }

    private static void resolveExperienceRequirement(WaystoneTeleportContext context, Player player, List<Object> requirements, List<Object> missing) {
        if (!WaystonesConfig.getActive().rules.enableXpCosts || !shouldCostExperience(context)) {
            return;
        }

        final int cost = getExperienceCost(context);
        final long available = getAvailableExperience(player);
        final var requirement = new ExperiencePointsRequirement(available, cost);
        requirements.add(requirement);
        if (available < cost) {
            missing.add(requirement);
        } else if (cost > 0 && !player.getAbilities().instabuild) {
            context.execute(id("xp_points_cost"), () -> player.giveExperiencePoints(-cost));
        }
    }

    private static boolean shouldCostExperience(WaystoneTeleportContext context) {
        final boolean isWarpStone = context.getWarpItem().is(ModItemTags.WARP_STONES);
        final boolean isFromWaystone = context.getFromWaystone()
                .filter(it -> it.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)).isPresent();
        final boolean isMemorial = context.getTargetWaystone().getWaystoneKind().equals(WaystoneKinds.FLEETING_MEMORIAL);
        return (isFromWaystone || isWarpStone) && !isMemorial;
    }

    private static int getExperienceCost(WaystoneTeleportContext context) {
        final double distance = Math.sqrt(context.getEntity().distanceToSqr(Vec3.atCenterOf(context.getTargetWaystone().getPos())));
        return context.isDimensionalTeleport() ? 27 : (int) Math.clamp(distance * 0.01, 0, 27);
    }

    private static long getAvailableExperience(Player player) {
        long available = (long) Math.floor(player.experienceProgress * player.getXpNeededForNextLevel());
        for (int level = 0; level < player.experienceLevel; level++) {
            available += level >= 30 ? 112 + (level - 30) * 9L : level >= 15 ? 37 + (level - 15) * 5L : 7 + level * 2L;
        }
        return available;
    }

    private static void resolveInventoryCooldownRequirement(WaystoneTeleportContext context, Player player, List<Object> requirements, List<Object> missing) {
        if (!WaystonesConfig.getActive().rules.enableCooldowns || !context.hasFlag(TeleportFlags.INVENTORY_BUTTON)) {
            return;
        }

        final var data = Balm.hooks().getPersistentData(player);
        final long now = player.level().getGameTime();
        final long remaining = Math.max(0L, data.getLongOr(INVENTORY_COOLDOWN, 0L) - now);
        final var requirement = new CooldownRequirement(id("inventory_button"), remaining, INVENTORY_COOLDOWN_TICKS, System.currentTimeMillis(), 50_000_000L);
        requirements.add(requirement);
        if (remaining > 0) {
            missing.add(requirement);
        } else if (!player.getAbilities().instabuild) {
            context.execute(id("inventory_button_cooldown"), () -> data.putLong(INVENTORY_COOLDOWN, player.level().getGameTime() + INVENTORY_COOLDOWN_TICKS));
        }
    }

    private static void applyDurabilityCost(WaystoneTeleportContext context, Player player) {
        final var warpItem = context.getWarpItem();
        if (player.getAbilities().instabuild || !WaystonesConfig.getActive().rules.enableDurability
                || !warpItem.is(ModItemTags.WARP_STONES) || !warpItem.isDamageableItem()
                || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        context.execute(id("damage_warp_stone"), () -> {
            final var hand = context.getWarpHand();
            if (hand != null) {
                warpItem.hurtAndBreak(80, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            } else {
                warpItem.hurtAndBreak(80, serverPlayer.level(), serverPlayer, _ -> {});
            }
        });
    }
}
