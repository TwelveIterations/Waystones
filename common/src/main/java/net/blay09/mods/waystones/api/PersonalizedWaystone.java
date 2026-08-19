package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.Set;

public interface PersonalizedWaystone extends Waystone {
    Waystone getBackingWaystone();

    Optional<Component> getAlias();

    Set<Identifier> getConfiguredGroups();

    default boolean isHidden() {
        return false;
    }
}
