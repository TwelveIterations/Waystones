package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class EntityTeleportResult {
    private final Entity entity;
    private final TeleportDestination originalDestination;
    private final @Nullable TeleportDestination resolvedDestination;
    private final @Nullable WaystoneTeleportError error;

    public EntityTeleportResult(
            Entity entity,
            TeleportDestination originalDestination,
            @Nullable TeleportDestination resolvedDestination,
            @Nullable WaystoneTeleportError error
    ) {
        this.entity = entity;
        this.originalDestination = originalDestination;
        this.resolvedDestination = resolvedDestination;
        this.error = error;
    }

    public static EntityTeleportResult success(Entity entity, TeleportDestination originalDestination, TeleportDestination resolvedDestination) {
        return new EntityTeleportResult(entity, originalDestination, resolvedDestination, null);
    }

    public static EntityTeleportResult failed(Entity entity, TeleportDestination originalDestination, WaystoneTeleportError error) {
        return new EntityTeleportResult(entity, originalDestination, null, error);
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
}
