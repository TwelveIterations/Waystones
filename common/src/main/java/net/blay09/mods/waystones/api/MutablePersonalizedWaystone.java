package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface MutablePersonalizedWaystone extends PersonalizedWaystone {
    void setAlias(@Nullable Component alias);

    void setConfiguredGroups(Collection<ResourceLocation> configuredGroups);

    default void setHidden(boolean hidden) {
    }
}
