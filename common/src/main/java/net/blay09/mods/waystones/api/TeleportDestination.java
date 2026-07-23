package net.blay09.mods.waystones.api;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record TeleportDestination(Level level, Vec3 location, Direction direction) {
}
