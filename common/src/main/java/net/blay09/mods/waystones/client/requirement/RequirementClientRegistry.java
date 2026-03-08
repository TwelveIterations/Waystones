package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.blay09.mods.waystones.requirement.*;

import java.util.*;

public class RequirementClientRegistry {

    private final static Map<Class<?>, RequirementRenderer<?>> renderers = new HashMap<>();
    private final static Map<Class<?>, RequirementMerger<?>> mergers = new HashMap<>();
    private final static CombinedRequirementRenderer LIST_RENDERER = new CombinedRequirementRenderer();

    public static <T> void registerRenderer(Class<? extends T> displayClass, RequirementRenderer<T> renderer) {
        renderers.put(displayClass, renderer);
    }

    public static <T> void registerMerger(Class<? extends T> requirementClass, RequirementMerger<T> merger) {
        mergers.put(requirementClass, merger);
    }

    @SuppressWarnings("unchecked")
    public static <T> RequirementRenderer<T> getRenderer(Class<T> displayClass) {
        return (RequirementRenderer<T>) renderers.get(displayClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> RequirementRenderer<T> getRenderer(T requirement) {
        return (RequirementRenderer<T>) renderers.get(requirement.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> RequirementMerger<T> getMerger(T requirement) {
        return (RequirementMerger<T>) mergers.get(requirement.getClass());
    }

    public static RequirementRenderer<List<Object>> getListRenderer() {
        return LIST_RENDERER;
    }

    public static List<Object> mergeRequirements(List<Object> requirements) {
        final var merged = new ArrayList<>(requirements.size());
        for (final var requirement : requirements) {
            final var merger = getMerger(requirement);
            if (merger == null) {
                merged.add(requirement);
                continue;
            }

            boolean didMerge = false;
            for (int i = 0; i < merged.size(); i++) {
                final var existing = merged.get(i);
                if (existing.getClass() != requirement.getClass()) {
                    continue;
                }

                final var mergedResult = merger.tryMerge(existing, requirement);
                if (mergedResult.isPresent()) {
                    merged.set(i, mergedResult.get());
                    didMerge = true;
                    break;
                }
            }

            if (!didMerge) {
                merged.add(requirement);
            }
        }
        return merged;
    }

    public static void registerDefaults() {
        registerRenderer(CooldownInformation.class, new CooldownRequirementRenderer());
        registerRenderer(CooldownInformation.class, new SoftCooldownRequirementRenderer());
        registerRenderer(ExperienceLevelCostInformation.class, new ExperienceLevelRequirementRenderer());
        registerRenderer(ExperiencePointsCostInformation.class, new ExperiencePointsRequirementRenderer());
        registerRenderer(ItemCostInformation.class, new ItemRequirementRenderer());
        registerRenderer(RefusalInformation.class, new RefuseRequirementRenderer());
        registerMerger(ExperienceLevelCostInformation.class, (current, incoming) -> Optional.of(new ExperienceLevelCostInformation(
                Math.min(current.available(), incoming.available()),
                current.required() + incoming.required()
        )));
        registerMerger(ExperiencePointsCostInformation.class, (current, incoming) -> Optional.of(new ExperiencePointsCostInformation(
                Math.min(current.available(), incoming.available()),
                current.required() + incoming.required()
        )));
        registerMerger(ItemCostInformation.class, (current, incoming) -> {
            if (!current.item().equals(incoming.item())) {
                return Optional.empty();
            }
            return Optional.of(new ItemCostInformation(
                    current.item(),
                    Math.min(current.available(), incoming.available()),
                    current.required() + incoming.required()
            ));
        });
    }
}
