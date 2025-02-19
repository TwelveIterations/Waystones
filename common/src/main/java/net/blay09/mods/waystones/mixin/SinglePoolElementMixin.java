package net.blay09.mods.waystones.mixin;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.worldgen.WaystoneStructurePoolElement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin implements WaystoneStructurePoolElement {

    @Unique
    private static final Set<BlockPos> waystones$generatedWaystones = new HashSet<>();

    @Unique
    private Boolean waystones$isWaystone;

    @Accessor
    public abstract Either<ResourceLocation, StructureTemplate> getTemplate();

    @Override
    public boolean waystones$isWaystone() {
        if (waystones$isWaystone == null) {
            final var template = getTemplate();
            template.ifLeft(resourceLocation -> waystones$isWaystone = resourceLocation.getPath().startsWith("village/") && resourceLocation.getPath().endsWith("/waystone"));
            template.ifRight(structureTemplate -> waystones$isWaystone = false);
        }
        return waystones$isWaystone;
    }

    @Override
    public void waystones$setIsWaystone(boolean isWaystone) {
        this.waystones$isWaystone = isWaystone;
    }

    @Inject(method = "place(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;Z)Z", at = @At("HEAD"), cancellable = true)
    public void place(StructureTemplateManager structureTemplateManager, WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos blockPos2, Rotation rotation, BoundingBox boundingBox, RandomSource randomSource, boolean bl, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (waystones$isWaystone()) {
            for (BlockPos existingPos : waystones$generatedWaystones) {
                // place is called separately for waystones crossing chunk borders, but the two blockpos parameters will be unique per generated waystone
                // therefore, only block nearby waystones if it's not literally the waystone that's supposed to be blocking it
                // future blay will smh at past blay when this breaks due to relying on an identity check instead of comparing the BlockPos values
                if (pos != existingPos && existingPos.distSqr(pos) < 100 * 100) {
                    callbackInfo.setReturnValue(false);
                    return;
                }
            }
            waystones$generatedWaystones.add(pos);
        }
    }

}
