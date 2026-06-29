package net.blay09.mods.waystones.api;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Waystone {
    UUID getWaystoneUid();

    /**
     * @return the base name of this waystone, as it was set by its owner
     */
    Component getName();

    /**
     * @return the display name of this waystone with modifiers like aliases applied
     */
    default Component getEffectiveName() {
        return getName();
    }

    ResourceKey<Level> getDimension();

    default boolean wasGenerated() {
        return getOrigin() == WaystoneOrigin.VILLAGE || getOrigin() == WaystoneOrigin.WILDERNESS || getOrigin() == WaystoneOrigin.DUNGEON;
    }

    WaystoneOrigin getOrigin();

    boolean isOwner(Player player);

    BlockPos getPos();

    boolean isValid();

    Optional<UUID> getOwnerUid();

    Identifier getWaystoneKind();

    default boolean hasName() {
        return !getName().getString().isEmpty();
    }

    default boolean wasSeen() {
        return hasName();
    }

    default boolean hasOwner() {
        return getOwnerUid().isPresent();
    }

    default boolean isValidInLevel(ServerLevel level) {
        return false;
    }

    default Optional<TeleportDestination> resolveDestination(ServerLevel level) {
        final var pos = getPos();
        final var state = level.getBlockState(pos);
        var direction = state.hasProperty(WaystoneBlock.FACING) ? state.getValue(WaystoneBlock.FACING) : Direction.NORTH;

        // Use a list to keep order intact - it might check one direction twice, but no one cares
        final var directionCandidates = Lists.newArrayList(direction, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH);
        for (Direction candidate : directionCandidates) {
            BlockPos offsetPos = pos.relative(candidate);
            BlockPos offsetPosUp = offsetPos.above();
            if (level.getBlockState(offsetPos).isSuffocating(level, offsetPos) || level.getBlockState(offsetPosUp).isSuffocating(level, offsetPosUp)) {
                continue;
            }

            direction = candidate;
            break;
        }

        final var waystoneType = getWaystoneKind();
        final var shouldOffsetFacing = !(waystoneType.equals(WaystoneKinds.WARP_PLATE));
        final var targetPos = shouldOffsetFacing ? pos.relative(direction) : pos;
        final var location = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        return Optional.of(new TeleportDestination(level, location, direction));
    }

    boolean isTransient();

    WaystoneVisibility getVisibility();

    default Set<Identifier> getWaystoneGroups() {
        return WaystonesAPI.getDynamicWaystoneGroups(this);
    }
}
