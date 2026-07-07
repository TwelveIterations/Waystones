package net.blay09.mods.waystones.requirement;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface RequirementComponentResolver<T> {
    Component resolve(T requirement);
}
