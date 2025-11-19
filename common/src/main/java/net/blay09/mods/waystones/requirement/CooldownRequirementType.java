package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.WaystoneCooldowns;
import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;

public class CooldownRequirementType implements RequirementType<CooldownRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "cooldown");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public CooldownRequirement createInstance() {
        return new CooldownRequirement(WaystoneCooldowns.INVENTORY_BUTTON, 0);
    }
}
