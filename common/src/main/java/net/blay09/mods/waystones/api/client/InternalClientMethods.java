package net.blay09.mods.waystones.api.client;

import net.blay09.mods.waystones.client.requirement.RequirementRenderer;
import net.blay09.mods.waystones.client.requirement.RequirementMerger;

public interface InternalClientMethods {
    <T> void registerRequirementRenderer(Class<T> clazz, RequirementRenderer<T> renderer);

    <T> void registerRequirementMerger(Class<T> clazz, RequirementMerger<T> merger);
}
