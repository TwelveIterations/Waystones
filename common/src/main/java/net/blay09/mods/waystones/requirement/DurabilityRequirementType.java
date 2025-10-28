package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.ResourceLocation;

public class DurabilityRequirementType implements RequirementType<DurabilityRequirement> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("waystones", "durability");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public DurabilityRequirement createInstance() {
        return new DurabilityRequirement(0);
    }
}
