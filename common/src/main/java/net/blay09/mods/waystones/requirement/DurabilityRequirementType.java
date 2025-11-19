package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;

public class DurabilityRequirementType implements RequirementType<DurabilityRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "durability");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public DurabilityRequirement createInstance() {
        return new DurabilityRequirement(0);
    }
}
