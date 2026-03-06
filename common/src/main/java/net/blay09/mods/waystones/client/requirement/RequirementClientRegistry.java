package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCostInformation;
import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.blay09.mods.waystones.requirement.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementClientRegistry {

    private final static Map<Class<?>, RequirementRenderer<?>> renderers = new HashMap<>();
    private final static CombinedRequirementRenderer LIST_RENDERER = new CombinedRequirementRenderer();

    public static <T> void registerRenderer(Class<? extends T> displayClass, RequirementRenderer<T> renderer) {
        renderers.put(displayClass, renderer);
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
    public static RequirementRenderer<List<Object>> getListRenderer() {
        return LIST_RENDERER;
    }

    public static void registerDefaults() {
        registerRenderer(CooldownInformation.class, new CooldownRequirementRenderer());
        registerRenderer(CooldownInformation.class, new SoftCooldownRequirementRenderer());
        registerRenderer(ExperienceLevelCostInformation.class, new ExperienceLevelRequirementRenderer());
        registerRenderer(ExperiencePointsCostInformation.class, new ExperiencePointsRequirementRenderer());
        registerRenderer(ItemCostInformation.class, new ItemRequirementRenderer());
        registerRenderer(RefusalInformation.class, new RefuseRequirementRenderer());
    }
}
