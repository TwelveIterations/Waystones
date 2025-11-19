package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.RequirementType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ItemRequirementType implements RequirementType<ItemRequirement> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("waystones", "item");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ItemRequirement createInstance() {
        return new ItemRequirement(ItemStack.EMPTY, 0);
    }
}
