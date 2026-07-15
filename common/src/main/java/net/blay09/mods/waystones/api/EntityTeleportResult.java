package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class EntityTeleportResult {
    private final Entity entity;
    private final TeleportDestination originalDestination;
    private final @Nullable TeleportDestination resolvedDestination;
    private final @Nullable WaystoneTeleportError error;
    private final @Nullable Entity originalVehicle;
    private final int passengerIndex;

    public EntityTeleportResult(
            Entity entity,
            TeleportDestination originalDestination,
            @Nullable TeleportDestination resolvedDestination,
            @Nullable WaystoneTeleportError error
    ) {
        this(entity, originalDestination, resolvedDestination, error, null, -1);
    }

    public EntityTeleportResult(
            Entity entity,
            TeleportDestination originalDestination,
            @Nullable TeleportDestination resolvedDestination,
            @Nullable WaystoneTeleportError error,
            @Nullable Entity originalVehicle,
            int passengerIndex
    ) {
        this.entity = entity;
        this.originalDestination = originalDestination;
        this.resolvedDestination = resolvedDestination;
        this.error = error;
        this.originalVehicle = originalVehicle;
        this.passengerIndex = passengerIndex;
    }

    public static EntityTeleportResult success(Entity entity, TeleportDestination originalDestination, TeleportDestination resolvedDestination) {
        return new EntityTeleportResult(entity, originalDestination, resolvedDestination, null);
    }

    public static EntityTeleportResult failed(Entity entity, TeleportDestination originalDestination, WaystoneTeleportError error) {
        return new EntityTeleportResult(entity, originalDestination, null, error);
    }

    public EntityTeleportResult withOriginalVehicle(@Nullable Entity originalVehicle, int passengerIndex) {
        return new EntityTeleportResult(entity, originalDestination, resolvedDestination, error, originalVehicle, passengerIndex);
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public Entity entity() {
        return entity;
    }

    public TeleportDestination originalDestination() {
        return originalDestination;
    }

    public @Nullable TeleportDestination resolvedDestination() {
        return resolvedDestination;
    }

    public @Nullable WaystoneTeleportError error() {
        return error;
    }

    public Optional<Entity> originalVehicle() {
        return Optional.ofNullable(originalVehicle);
    }

    public int passengerIndex() {
        return passengerIndex;
    }
}
