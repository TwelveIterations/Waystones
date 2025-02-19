package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("WeakerAccess")
@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public class JigsawPlacementPlacerMixin {

    @Final
    @Shadow
    private Registry<StructureTemplatePool> pools;

    private boolean hasWaystone;

    private boolean shouldForceWaystone() {
        return WaystonesConfig.getActive().worldGen.forceSpawnInVillages;
    }

    @ModifyArg(method = "tryPlacingChildren(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IZLnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private ResourceKey<StructureTemplatePool> forceWaystonePool(ResourceKey<StructureTemplatePool> resourceKey) {
        if (!shouldForceWaystone() || hasWaystone) {
            return resourceKey;
        }

        String poolPath = resourceKey.location().getPath();
        if (poolPath.endsWith("/houses")) {
            ResourceLocation waystonePoolName = new ResourceLocation(Waystones.MOD_ID, poolPath.replace("/houses", "/waystones"));
            ResourceKey<StructureTemplatePool> waystonePoolKey = ResourceKey.create(Registries.TEMPLATE_POOL, waystonePoolName);
            if (pools.getHolder(waystonePoolKey).isPresent()) {
                hasWaystone = true;
                return waystonePoolKey;
            }
        }

        return resourceKey;
    }

}
