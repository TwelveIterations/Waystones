package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.core.WaystoneTeleportedEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;

@Mixin(Entity.class)
public class EntityMixin implements WaystoneTeleportedEntity {

    @Unique
    private boolean waystones$teleportedByWaystone;

    @Unique
    private int waystones$ticksPassedOnWarpPlate;

    @Unique
    private int waystones$ticksPassedSinceWarpPlate;

    @Unique
    private WeakReference<WarpPlateBlockEntity> waystones$lastWarpPlate;

    @Override
    public void waystones$markTeleportedByWaystone() {
        waystones$teleportedByWaystone = true;
    }

    @Override
    public boolean waystones$consumeTeleportedByWaystone() {
        if (waystones$teleportedByWaystone) {
            waystones$teleportedByWaystone = false;
            return true;
        }

        return false;
    }

    @Override
    public int waystones$getTicksPassedOnWarpPlate() {
        return waystones$ticksPassedOnWarpPlate;
    }

    @Override
    public void waystones$setTicksPassedOnWarpPlate(int ticksPassed) {
        waystones$ticksPassedOnWarpPlate = ticksPassed;
    }

    @Override
    public int waystones$getTicksPassedSinceWarpPlate() {
        return waystones$ticksPassedSinceWarpPlate;
    }

    @Override
    public void waystones$setTicksPassedSinceWarpPlate(int ticksPassed) {
        waystones$ticksPassedSinceWarpPlate = ticksPassed;
    }

    @Override
    public WarpPlateBlockEntity waystones$getLastWarpPlate() {
        return waystones$lastWarpPlate != null ? waystones$lastWarpPlate.get() : null;
    }

    @Override
    public void waystones$setLastWarpPlate(WarpPlateBlockEntity warpPlate) {
        waystones$lastWarpPlate = warpPlate != null ? new WeakReference<>(warpPlate) : null;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void waystones$tickWarpPlateCooldown(CallbackInfo ci) {
        WarpPlateBlockEntity.tickEntityWarpPlateState((Entity) (Object) this, this);
    }
}
