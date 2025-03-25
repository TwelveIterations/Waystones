package net.blay09.mods.waystones.compat;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RepurposedStructuresIntegration {
    public static final DeferredRegister<Supplier<Boolean>> RS_CONDITIONS_REGISTRY = DeferredRegister.create(
            ResourceLocation.fromNamespaceAndPath("repurposed_structures", "json_conditions"), "waystones");

    public static final DeferredHolder<Supplier<Boolean>, Supplier<Boolean>> WAYSTONE_CONFIG_CONDITION = RS_CONDITIONS_REGISTRY.register(
            "config", () -> () -> WaystonesConfig.getActive().worldGen.spawnInVillages != WaystonesConfig.VillageWaystoneGeneration.DISABLED);

    public RepurposedStructuresIntegration() {
        final var modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        RS_CONDITIONS_REGISTRY.register(modEventBus);
    }
}
