package net.blay09.mods.waystones.requirement;

import net.blay09.mods.shogi.coercion.ComponentHolder;
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
                .or(() -> requirements.stream().findFirst().map(RequirementComponentResolvers::getFallbackReason))
                .map(reason -> Component.translatable("chat.waystones.requirements_not_met.specific", reason))
                .orElseGet(() -> Component.translatable("chat.waystones.requirements_not_met"));
    }

    public static Component getFallbackReason(Object requirement) {
        if (requirement instanceof ComponentHolder componentHolder) {
            return componentHolder.component();
        }

        return Component.literal(requirement.toString());
    }

    public static void registerDefaults() {
        register(ModifierRequirement.class, requirement -> {
            final var itemStack = requirement.item().stream().findFirst().map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
            return Component.translatable(requirement.translationKey(), requirement.required(), itemStack.getHoverName());
        });
    }
}
