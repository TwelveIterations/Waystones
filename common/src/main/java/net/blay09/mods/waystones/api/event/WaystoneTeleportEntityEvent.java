package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.EntityTeleportResult;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Fired for each individual entity that is teleported by Waystones.
 */
public abstract class WaystoneTeleportEntityEvent extends BalmEvent {

    private final WaystoneTeleportContext context;
    private final Entity entity;
    private final TeleportDestination originalDestination;
    private ServerLevel targetLevel;
    private Vec3 targetPosition;
    private Direction direction;

    public WaystoneTeleportEntityEvent(WaystoneTeleportContext context, Entity entity, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
        this.context = context;
        this.entity = entity;
        this.originalDestination = originalDestination;
        this.targetLevel = targetLevel;
        this.targetPosition = targetPosition;
        this.direction = direction;
    }

    public WaystoneTeleportContext getContext() {
        return context;
    }

    public Entity getEntity() {
        return entity;
    }

    public TeleportDestination getOriginalDestination() {
        return originalDestination;
    }

    public ServerLevel getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(ServerLevel targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Vec3 getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Vec3 targetPosition) {
        this.targetPosition = targetPosition;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static class Pre extends WaystoneTeleportEntityEvent {
        private @Nullable EntityTeleportResult overrideResult;

        public Pre(WaystoneTeleportContext context, Entity entity, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, originalDestination, targetLevel, targetPosition, direction);
        }

        public Optional<EntityTeleportResult> getOverrideResult() {
            return Optional.ofNullable(overrideResult);
        }

        public void overrideResult(@Nullable EntityTeleportResult result) {
            this.overrideResult = result;
        }
    }

    public static class Post extends WaystoneTeleportEntityEvent {
        private final Entity teleportedEntity;
        private final EntityTeleportResult result;

        public Post(WaystoneTeleportContext context, Entity entity, EntityTeleportResult result, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, originalDestination, targetLevel, targetPosition, direction);
            this.teleportedEntity = result.entity();
            this.result = result;
        }

        public Post(WaystoneTeleportContext context, Entity entity, Entity teleportedEntity, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            this(context,
                    entity,
                    EntityTeleportResult.success(teleportedEntity, originalDestination, new TeleportDestination(targetLevel, targetPosition, direction)),
                    originalDestination,
                    targetLevel,
                    targetPosition,
                    direction);
        }

        public Entity getTeleportedEntity() {
            return teleportedEntity;
        }

        public EntityTeleportResult getResult() {
            return result;
        }
    }
}
