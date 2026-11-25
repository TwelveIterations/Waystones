package net.blay09.mods.waystones.compat.shogi;

import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.waystones.requirement.RequirementComponentResolvers.register;

public class ShogiRequirementComponentResolvers {
    public static void registerDefaults() {
        register(CooldownInformation.class, requirement -> Component.translatable("tooltip.waystones.cooldown_left", getMillisLeft(requirement) / 1000));
        register(ExperienceLevelCostInformation.class, requirement -> Component.translatable("gui.waystones.waystone_selection.level_requirement", requirement.required()));
        register(ExperiencePointsCostInformation.class, requirement -> Component.translatable("gui.waystones.waystone_selection.xp_requirement", requirement.required()));
        register(ItemCostInformation.class, requirement -> {
            final var itemStack = requirement.item().stream().findFirst().map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
            return Component.translatable("gui.waystones.waystone_selection.item_requirement", requirement.required(), itemStack.getHoverName());
        });
        register(RefusalInformation.class, RefusalInformation::message);
    }

    private static long getMillisLeft(CooldownInformation requirement) {
        if (requirement.nanosecondsPerTick() <= 0L) {
            return 0L;
        }

        final long totalNanos = Math.max(0L, requirement.remainingTicks()) * requirement.nanosecondsPerTick();
        final long elapsedMillis = Math.max(0L, System.currentTimeMillis() - requirement.nowUnixMs());
        return Math.max(0L, totalNanos / 1_000_000L - elapsedMillis);
    }
}
