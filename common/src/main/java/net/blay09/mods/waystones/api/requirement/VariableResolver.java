package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.Identifier;

public interface VariableResolver {
    Identifier getId();
    float resolve(WaystoneTeleportContext context);
}
