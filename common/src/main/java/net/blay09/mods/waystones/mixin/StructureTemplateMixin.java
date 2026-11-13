package net.blay09.mods.waystones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.blay09.mods.waystones.worldgen.StructureAwareBlockEntity;
import net.blay09.mods.waystones.worldgen.StructureAwareBlockEntityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Shadow
    @Final
    private List<StructureTemplate.Palette> palettes;

    @Inject(method = "fillFromWorld", at = @At("RETURN"))
    private void fillFromWorld(Level level, BlockPos origin, Vec3i size, boolean includeEntities, List<Block> ignored, CallbackInfo ci) {
        if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
            StructureAwareBlockEntityHooks.rewriteStructureAwareBlockEntities(level, origin, size, palettes);
        }
    }

    @WrapOperation(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/world/level/storage/ValueInput;)V"))
    private void placeInWorldLoadWithComponents(BlockEntity entity, ValueInput input, Operation<Void> original, ServerLevelAccessor level, BlockPos position, BlockPos referencePos, StructurePlaceSettings settings, RandomSource random, int flags) {
        original.call(entity, input);
        if (entity instanceof StructureAwareBlockEntity structureEntity) {
            structureEntity.loadStructureData(settings);
        }
    }
}
