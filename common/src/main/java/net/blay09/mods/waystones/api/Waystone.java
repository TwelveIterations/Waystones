package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

    default Optional<String> getOwnerUsername() {
        return Optional.empty();
    }

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
        return WaystonesAPI.resolveDefaultDestination(level, this);
    }

    boolean isTransient();

    WaystoneVisibility getVisibility();

    default Set<Identifier> getWaystoneGroups() {
        return WaystonesAPI.getDynamicWaystoneGroups(this);
    }
}
