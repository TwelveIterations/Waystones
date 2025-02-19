package net.blay09.mods.waystones.api;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TeleportDestination {
    private final ServerLevel level;
    private final Vec3 location;
    private final Direction direction;

    public TeleportDestination(ServerLevel level, Vec3 location, Direction direction) {
        this.level = level;
        this.location = location;
        this.direction = direction;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public Vec3 getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }
}
