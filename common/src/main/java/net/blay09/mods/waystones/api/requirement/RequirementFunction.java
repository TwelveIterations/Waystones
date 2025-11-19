package net.blay09.mods.waystones.api.requirement;

import net.minecraft.resources.Identifier;

public interface RequirementFunction<TRequirement extends WarpRequirement, TParameter> extends WarpRequirementModifierFunction<TRequirement, TParameter> {
    Identifier getId();

    Identifier getRequirementType();

    Class<TParameter> getParameterType();

    boolean isEnabled();
}

