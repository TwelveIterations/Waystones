package net.blay09.mods.waystones.compat;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class RepurposedStructuresIntegration {
    @SuppressWarnings("unchecked")
    public RepurposedStructuresIntegration() {
        BuiltInRegistries.REGISTRY.getOptional(Identifier.fromNamespaceAndPath("repurposed_structures", "json_conditions"))
                .ifPresent(registry -> Registry.register(
                        (Registry<Supplier<Boolean>>) registry,
                        Identifier.fromNamespaceAndPath("waystones", "config"),
                        () -> WaystonesConfig.getActive().worldGen.spawnInVillages != WaystonesConfig.VillageWaystoneGeneration.DISABLED));
    }
}
