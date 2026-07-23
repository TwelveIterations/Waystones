package net.blay09.mods.waystones.client.requirement;

import java.util.Optional;

@FunctionalInterface
public interface RequirementMerger<T> {
    Optional<T> tryMerge(T current, T incoming);
}
