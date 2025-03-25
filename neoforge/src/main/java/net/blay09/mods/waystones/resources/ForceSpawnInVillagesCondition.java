package net.blay09.mods.waystones.resources;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ForceSpawnInVillagesCondition implements ICondition {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Waystones.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ForceSpawnInVillagesCondition>> FORCE_SPAWN_IN_VILLAGES = CONDITION_TYPES.register("force_spawn_in_villages", () -> ForceSpawnInVillagesCondition.CODEC);

    public static final MapCodec<ForceSpawnInVillagesCondition> CODEC = MapCodec.unit(ForceSpawnInVillagesCondition::new);

    @Override
    public boolean test(IContext iContext) {
        return WaystonesConfig.getActive().worldGen.spawnInVillages == WaystonesConfigData.VillageWaystoneGeneration.FREQUENT;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public static void initialize() {

    }
}
