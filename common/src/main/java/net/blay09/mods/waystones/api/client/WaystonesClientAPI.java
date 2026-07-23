package net.blay09.mods.waystones.api.client;

import net.blay09.mods.waystones.client.requirement.RequirementRenderer;
import net.blay09.mods.waystones.client.requirement.RequirementMerger;

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

    public static <T> void registerRequirementRenderer(Class<T> clazz, RequirementRenderer<T> renderer) {
        __internalMethods.registerRequirementRenderer(clazz, renderer);
    }

    public static <T> void registerRequirementMerger(Class<T> clazz, RequirementMerger<T> merger) {
        __internalMethods.registerRequirementMerger(clazz, merger);
    }
}
