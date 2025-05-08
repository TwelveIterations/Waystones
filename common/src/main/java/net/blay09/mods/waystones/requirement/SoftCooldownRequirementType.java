package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.ResourceLocation;

public class SoftCooldownRequirementType implements RequirementType<SoftCooldownRequirement> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("waystones", "soft_cooldown");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public SoftCooldownRequirement createInstance() {
        return new SoftCooldownRequirement(WaystoneCooldowns.INVENTORY_BUTTON, 0);
    }
}
