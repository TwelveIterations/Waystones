package net.blay09.mods.waystones.core;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface WaystoneTeleportedEntity {
    int waystones$getTicksPassedOnWarpPlate();
    void waystones$setTicksPassedOnWarpPlate(int ticksPassed);
    int waystones$getTicksPassedSinceWarpPlate();
    void waystones$setTicksPassedSinceWarpPlate(int ticksPassed);
    @Nullable UUID waystones$getLastWarpPlate();
    void waystones$setLastWarpPlate(@Nullable UUID warpPlateUid);
}
