package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;

public class ExperienceLevelRequirementType implements RequirementType<ExperienceLevelRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "experience_levels");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ExperienceLevelRequirement createInstance() {
        return new ExperienceLevelRequirement(0);
    }
}
