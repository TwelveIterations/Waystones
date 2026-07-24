package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import org.jetbrains.annotations.Nullable;

public interface WaystoneTeleportedEntity {

    void waystones$markTeleportedByWaystone();

    boolean waystones$consumeTeleportedByWaystone();

    int waystones$getTicksPassedOnWarpPlate();

    void waystones$setTicksPassedOnWarpPlate(int ticksPassed);

    int waystones$getTicksPassedSinceWarpPlate();

    void waystones$setTicksPassedSinceWarpPlate(int ticksPassed);

    @Nullable
    WarpPlateBlockEntity waystones$getLastWarpPlate();

    void waystones$setLastWarpPlate(@Nullable WarpPlateBlockEntity warpPlate);
}
