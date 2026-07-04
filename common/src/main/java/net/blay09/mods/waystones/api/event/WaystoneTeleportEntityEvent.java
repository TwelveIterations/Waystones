package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class WaystoneTeleportEntityEvent extends BalmEvent {

    private final WaystoneTeleportContext context;
    private final Entity entity;
    private ServerLevel targetLevel;
    private Vec3 targetPosition;
    private Direction direction;

    public WaystoneTeleportEntityEvent(WaystoneTeleportContext context, Entity entity, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
        this.context = context;
        this.entity = entity;
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
        public Pre(WaystoneTeleportContext context, Entity entity, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, targetLevel, targetPosition, direction);
        }
    }

    public static class Post extends WaystoneTeleportEntityEvent {
        private final Entity teleportedEntity;

        public Post(WaystoneTeleportContext context, Entity entity, Entity teleportedEntity, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, targetLevel, targetPosition, direction);
            this.teleportedEntity = teleportedEntity;
        }

        public Entity getTeleportedEntity() {
            return teleportedEntity;
        }
    }
}
