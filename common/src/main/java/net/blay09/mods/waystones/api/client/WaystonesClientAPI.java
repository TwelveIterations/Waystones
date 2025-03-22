package net.blay09.mods.waystones.api.client;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.client.requirement.RequirementRenderer;

import java.lang.reflect.InvocationTargetException;

public class WaystonesClientAPI {

    public static final InternalClientMethods __internalMethods;

    static {
        try {
            __internalMethods = (InternalClientMethods) Class.forName("net.blay09.mods.waystones.client.InternalClientMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends WarpRequirement> void registerRequirementRenderer(Class<T> clazz, RequirementRenderer<T> renderer) {
        __internalMethods.registerRequirementRenderer(clazz, renderer);
    }
}
