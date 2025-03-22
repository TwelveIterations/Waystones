package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.api.client.InternalClientMethods;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.blay09.mods.waystones.client.requirement.RequirementRenderer;

public class InternalClientMethodsImpl implements InternalClientMethods {
    @Override
    public <T extends WarpRequirement> void registerRequirementRenderer(Class<T> clazz, RequirementRenderer<T> renderer) {
        RequirementClientRegistry.registerRenderer(clazz, renderer);
    }
}
