package net.blay09.mods.waystones.api.requirement;

import net.minecraft.resources.Identifier;

public interface RequirementType<T extends WarpRequirement> {
    Identifier getId();
    T createInstance();
}
