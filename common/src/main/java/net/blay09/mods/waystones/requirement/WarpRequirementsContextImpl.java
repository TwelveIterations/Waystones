package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.api.requirement.WarpRequirementsContext;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class WarpRequirementsContextImpl implements WarpRequirementsContext {

    private final Map<Identifier, WarpRequirement> requirements = new HashMap<>();
    private final WaystoneTeleportContext context;

    public WarpRequirementsContextImpl(WaystoneTeleportContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T extends WarpRequirement, P> void apply(ConfiguredRequirementModifier<T, P> configuredModifier) {
        for (final var condition : configuredModifier.conditions()) {
            if (!matchesCondition(condition)) {
                return;
            }
        }

        final var requirement = configuredModifier.requirement();
        final var modifier = requirement.modifier();
        final var parameters = requirement.parameters();
        var existing = (T) requirements.get(modifier.getRequirementType());
        if (existing == null) {
            existing = RequirementRegistry.<T>getRequirementType(modifier.getRequirementType()).createInstance();
        }
        requirements.put(modifier.getRequirementType(), modifier.apply(existing, this, parameters));
    }

    public float getContextValue(Identifier id) {
        final var resolver = RequirementRegistry.getVariableResolver(id);
        if (resolver != null) {
            return resolver.resolve(context);
        }

        if (context.getEntity() instanceof Player player) {
            return PlayerWaystoneManager.getCooldownMillisLeft(player, id) / 1000f;
        }

        return 0f;
    }

    public <P> boolean matchesCondition(ConfiguredCondition<P> configuredCondition) {
        return configuredCondition.resolver().matches(context, configuredCondition.parameters());
    }

    public WarpRequirement resolve() {
        if (requirements.isEmpty()) {
            return NoRequirement.INSTANCE;
        } else if (requirements.size() == 1) {
            return requirements.values().iterator().next();
        }
        return new CombinedRequirement(requirements.values());
    }
}
