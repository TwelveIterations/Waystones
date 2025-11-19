package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.requirement.ConfiguredCondition;
import net.minecraft.resources.Identifier;

public interface WarpRequirementsContext {
    <P> boolean matchesCondition(ConfiguredCondition<P> configuredCondition);

    float getContextValue(Identifier id);
}
