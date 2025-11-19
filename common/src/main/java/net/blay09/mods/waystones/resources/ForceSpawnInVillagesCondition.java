package net.blay09.mods.waystones.resources;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.server.packs.resources.BalmResourceCondition;
import net.blay09.mods.balm.server.packs.resources.ResourceConditionContext;
import net.blay09.mods.waystones.config.WaystonesConfig;

public class ForceSpawnInVillagesCondition implements BalmResourceCondition {
    public static final MapCodec<ForceSpawnInVillagesCondition> CODEC = MapCodec.unit(ForceSpawnInVillagesCondition::new);

    @Override
    public boolean test(ResourceConditionContext resourceConditionContext) {
        return WaystonesConfig.getActive().worldGen.spawnInVillages == WaystonesConfig.VillageWaystoneGeneration.FREQUENT;
    }
}
