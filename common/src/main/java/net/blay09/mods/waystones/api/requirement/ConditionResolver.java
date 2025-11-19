package net.blay09.mods.waystones.api.requirement;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.Identifier;

public interface ConditionResolver<P> {
    Identifier getId();

    Class<P> getParameterType();

    boolean matches(WaystoneTeleportContext context, P parameters);
}
