package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public abstract class WaystoneTeleportEntityEvent {

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
        public static final BidirectionalEventMapper<Consumer<Pre>> EVENT = Balmstrap.createBoundCustomEvent(Pre.class);

        private boolean canceled;

        public Pre(WaystoneTeleportContext context, Entity entity, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, originalDestination, targetLevel, targetPosition, direction);
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public static class Post extends WaystoneTeleportEntityEvent {
        public static final BidirectionalEventMapper<Consumer<Post>> EVENT = Balmstrap.createBoundCustomEvent(Post.class);

        private final Entity teleportedEntity;

        public Post(WaystoneTeleportContext context, Entity entity, Entity teleportedEntity, TeleportDestination originalDestination, ServerLevel targetLevel, Vec3 targetPosition, Direction direction) {
            super(context, entity, originalDestination, targetLevel, targetPosition, direction);
            this.teleportedEntity = teleportedEntity;
        }

        public Entity getTeleportedEntity() {
            return teleportedEntity;
        }
    }
}
