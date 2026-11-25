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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class DefaultWarpRequirements {
    private static final String INVENTORY_COOLDOWN = "WaystonesInventoryButtonCooldown";

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
        final var config = WaystonesConfig.getActive().costs;
        if (context.isDimensionalTeleport()) {
            return Math.max(0, config.dimensionalXpCost);
        }
        final double distance = Math.sqrt(context.getEntity().distanceToSqr(Vec3.atCenterOf(context.getTargetWaystone().getPos())));
        final int maximum = Math.max(0, config.maxXpCost);
        final int minimum = Math.clamp(config.minXpCost, 0, maximum);
        final double distanceCost = distance * Math.max(0, config.xpCostPerBlock);
        final double cost = config.inverseXpCost ? maximum - distanceCost : distanceCost;
        return (int) Math.clamp(cost, minimum, maximum);
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

        final int cooldownTicks = getInventoryButtonCooldownTicks();
        if (cooldownTicks <= 0) {
            return;
        }

        final var data = Balm.hooks().getPersistentData(player);
        final long now = player.level().getGameTime();
        final long remaining = Math.max(0L, data.getLongOr(INVENTORY_COOLDOWN, 0L) - now);
        final var requirement = new CooldownRequirement(id("inventory_button"), remaining, cooldownTicks, System.currentTimeMillis(), 50_000_000L);
        requirements.add(requirement);
        if (remaining > 0) {
            missing.add(requirement);
        } else if (!player.getAbilities().instabuild) {
            context.execute(id("inventory_button_cooldown"), () -> data.putLong(INVENTORY_COOLDOWN, player.level().getGameTime() + cooldownTicks));
        }
    }

    private static int getInventoryButtonCooldownTicks() {
        return (int) Math.clamp((long) WaystonesConfig.getActive().costs.inventoryButtonCooldown * 20, 0L, Integer.MAX_VALUE);
    }

    private static int getDurabilityCost(ItemStack warpItem) {
        final int uses = WaystonesConfig.getActive().costs.warpStoneUses;
        return uses <= 0 ? 0 : (int) Math.ceil((double) warpItem.getMaxDamage() / uses);
    }

    private static void applyDurabilityCost(WaystoneTeleportContext context, Player player) {
        final var warpItem = context.getWarpItem();
        if (player.getAbilities().instabuild || !WaystonesConfig.getActive().rules.enableDurability
                || !warpItem.is(ModItemTags.WARP_STONES) || !warpItem.isDamageableItem()
                || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        final int damage = getDurabilityCost(warpItem);
        if (damage <= 0) {
            return;
        }

        context.execute(id("damage_warp_stone"), () -> {
            final var hand = context.getWarpHand();
            if (hand != null) {
                warpItem.hurtAndBreak(damage, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            } else {
                warpItem.hurtAndBreak(damage, serverPlayer.level(), serverPlayer, _ -> {});
            }
        });
    }
}
