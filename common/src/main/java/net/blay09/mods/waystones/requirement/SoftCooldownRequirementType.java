package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;

public class SoftCooldownRequirementType implements RequirementType<SoftCooldownRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "soft_cooldown");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public SoftCooldownRequirement createInstance() {
        return new SoftCooldownRequirement(WaystoneCooldowns.INVENTORY_BUTTON, 0);
    }
}
