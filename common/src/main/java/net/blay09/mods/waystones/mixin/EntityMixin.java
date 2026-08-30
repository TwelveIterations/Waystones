package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.core.WaystoneTeleportedEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin implements WaystoneTeleportedEntity {

    @Unique
    private int waystones$ticksPassedOnWarpPlate;

    @Unique
    private int waystones$warpPlateCooldownTicks;

    @Unique
    private @Nullable UUID waystones$lastWarpPlate;

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
        return waystones$warpPlateCooldownTicks;
    }

    @Override
    public void waystones$setTicksPassedSinceWarpPlate(int ticksPassed) {
        waystones$warpPlateCooldownTicks = ticksPassed;
    }

    @Override
    public @Nullable UUID waystones$getLastWarpPlate() {
        return waystones$lastWarpPlate;
    }

    @Override
    public void waystones$setLastWarpPlate(@Nullable UUID warpPlateUid) {
        waystones$lastWarpPlate = warpPlateUid;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    private void waystones$saveWarpPlateState(ValueOutput output, CallbackInfo ci) {
        output.putInt("WaystonesTicksOnWarpPlate", waystones$ticksPassedOnWarpPlate);
        output.putInt("WaystonesWarpPlateCooldown", waystones$warpPlateCooldownTicks);
        output.storeNullable("WaystonesLastWarpPlate", UUIDUtil.CODEC, waystones$lastWarpPlate);
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void waystones$loadWarpPlateState(ValueInput input, CallbackInfo ci) {
        waystones$ticksPassedOnWarpPlate = input.getIntOr("WaystonesTicksOnWarpPlate", 0);
        waystones$warpPlateCooldownTicks = input.getIntOr("WaystonesWarpPlateCooldown", 0);
        waystones$lastWarpPlate = input.read("WaystonesLastWarpPlate", UUIDUtil.CODEC).orElse(null);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void waystones$tickWarpPlateState(CallbackInfo ci) {
        WarpPlateBlockEntity.tickEntityWarpPlateState((Entity) (Object) this, this);
    }
}
