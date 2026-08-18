package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;

public interface PersonalizedWaystone extends Waystone {
    Waystone getBackingWaystone();

    Optional<Component> getAlias();

    Set<ResourceLocation> getConfiguredGroups();

    default boolean isHidden() {
        return false;
    }
}
