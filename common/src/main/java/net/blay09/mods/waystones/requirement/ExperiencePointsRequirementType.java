package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;

public class ExperiencePointsRequirementType implements RequirementType<ExperiencePointsRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "experience_points");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ExperiencePointsRequirement createInstance() {
        return new ExperiencePointsRequirement(0);
    }
}
