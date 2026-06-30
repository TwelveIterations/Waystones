package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.requirement.*;

import java.util.HashMap;
import java.util.Map;

public class RequirementClientRegistry {

    private final static Map<Class<? extends WarpRequirement>, RequirementRenderer<?>> renderers = new HashMap<>();

    public static <T extends WarpRequirement> void registerRenderer(Class<T> displayClass, RequirementRenderer<T> renderer) {
        renderers.put(displayClass, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends WarpRequirement> RequirementRenderer<T> getRenderer(Class<T> displayClass) {
        return (RequirementRenderer<T>) renderers.get(displayClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends WarpRequirement> RequirementRenderer<T> getRenderer(T requirement) {
        return (RequirementRenderer<T>) renderers.get(requirement.getClass());
    }

    public static void registerDefaults() {
        registerRenderer(CooldownRequirement.class, new CooldownRequirementRenderer());
        registerRenderer(SoftCooldownRequirement.class, new SoftCooldownRequirementRenderer());
        registerRenderer(ExperienceLevelRequirement.class, new ExperienceLevelRequirementRenderer());
        registerRenderer(ExperiencePointsRequirement.class, new ExperiencePointsRequirementRenderer());
        registerRenderer(ItemRequirement.class, new ItemRequirementRenderer());
        registerRenderer(TwinboundFeatherRequirement.class, new TwinboundFeatherRequirementRenderer());
        registerRenderer(EpitaphRequirement.class, new EpitaphRequirementRenderer());
        registerRenderer(RefuseRequirement.class, new RefuseRequirementRenderer());
        registerRenderer(CombinedRequirement.class, new CombinedRequirementRenderer());
    }
}
