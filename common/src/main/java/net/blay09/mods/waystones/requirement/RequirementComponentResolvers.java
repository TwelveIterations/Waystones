package net.blay09.mods.waystones.requirement;

import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequirementComponentResolvers {
    private static final Map<Class<?>, RequirementComponentResolver<?>> resolvers = new HashMap<>();

    public static <T> void register(Class<? extends T> requirementClass, RequirementComponentResolver<T> resolver) {
        resolvers.put(requirementClass, resolver);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Component> resolve(T requirement) {
        final var resolver = (RequirementComponentResolver<T>) resolvers.get(requirement.getClass());
        return resolver != null ? Optional.of(resolver.resolve(requirement)) : Optional.empty();
    }

    public static Optional<Component> resolve(List<Object> requirements) {
        for (final var requirement : requirements) {
            final var component = resolve(requirement);
            if (component.isPresent()) {
                return component;
            }
        }

        return Optional.empty();
    }

    public static Component resolveOrDefault(List<Object> requirements) {
        return resolve(requirements)
                .map(reason -> Component.translatable("chat.waystones.requirements_not_met.specific", reason))
                .orElseGet(() -> Component.translatable("chat.waystones.requirements_not_met"));
    }

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
