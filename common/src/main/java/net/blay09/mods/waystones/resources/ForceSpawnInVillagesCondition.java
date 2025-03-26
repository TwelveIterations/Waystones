package net.blay09.mods.waystones.resources;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.api.resources.BalmResourceCondition;
import net.blay09.mods.balm.api.resources.ResourceConditionContext;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;

public class ForceSpawnInVillagesCondition implements BalmResourceCondition {
    public static final MapCodec<ForceSpawnInVillagesCondition> CODEC = MapCodec.unit(ForceSpawnInVillagesCondition::new);

    @Override
    public boolean test(ResourceConditionContext resourceConditionContext) {
        return WaystonesConfig.getActive().worldGen.spawnInVillages == WaystonesConfigData.VillageWaystoneGeneration.FREQUENT;
    }
}
