package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class TwinboundFeatherWaystone extends WaystoneImpl {
    public TwinboundFeatherWaystone(UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, Component name) {
        super(WaystoneKinds.TWINBOUND_FEATHER, waystoneUid, dimension, pos, WaystoneOrigin.PLAYER, name, WaystoneVisibility.ACTIVATION);
    }

    @Override
    public boolean isValidInLevel(ServerLevel level) {
        return true;
    }

    @Override
    public Optional<TeleportDestination> resolveDestination(ServerLevel level) {
        final var pos = getPos();
        final var location = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return Optional.of(new TeleportDestination(level, location, Direction.NORTH));
    }

    @Override
    public boolean isTransient() {
        return true;
    }
}
