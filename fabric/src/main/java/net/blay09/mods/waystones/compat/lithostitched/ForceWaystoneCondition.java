package net.blay09.mods.waystones.compat.lithostitched;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ForceWaystoneCondition implements ResourceCondition {
    public static final MapCodec<ForceWaystoneCondition> CODEC = MapCodec.unit(ForceWaystoneCondition::new);
    public static final ResourceConditionType<ForceWaystoneCondition> TYPE = ResourceConditionType.create(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "force"), CODEC);

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoLookup registryInfoLookup) {
        return WaystonesConfig.getActive().worldGen.spawnInVillages == WaystonesConfigData.VillageWaystoneGeneration.FREQUENT;
    }

    public static void initialize() {
        ResourceConditions.register(TYPE);
    }
}
