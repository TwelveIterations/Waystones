package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.waystones.mixin.ChunkMapAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Rebuilds the entity trackers of teleported entities.
 * <p>
 * Vanilla only re-evaluates which players are able to see an entity when that entity's own chunk section changes.
 * A teleport can therefore drop the entity from a client that stayed within tracking range without ever sending it
 * again, leaving the entity alive on the server but missing on the client. Recreating the tracker forces the
 * destination area to be resynced.
 * <p>
 * This is not specific to this mod. It can be reproduced in Vanilla by having two players far away from each other,
 * spawning some donkeys, and running {@code /tp @e[type=donkey] @s} on the other one.
 */
public class EntityTrackingRefresher {

    /**
     * How long to keep waiting for an entity that landed in a chunk that still has to load.
     */
    private static final int MAX_PENDING_TICKS = 200;

    private record PendingRefresh(Entity entity, int queuedAtTick) {
    }

    private static final Logger logger = LogManager.getLogger();

    private static final List<PendingRefresh> pendingRefreshes = new ArrayList<>();

    public static void initialize() {
        Balm.getEvents().onTickEvent(TickType.ServerLevel, TickPhase.End, level -> {
            if (level instanceof ServerLevel serverLevel) {
                processPendingRefreshes(serverLevel);
            }
        });
    }

    public static void refresh(Entity entity) {
        // Players stay in sync through their own connection. Removing them from the chunk map would also drop the
        // chunk tickets and player map entries their connection depends on.
        if (entity instanceof ServerPlayer || entity.isRemoved() || !(entity.level() instanceof ServerLevel level)) {
            return;
        }

        if (tryRefresh(entity, level)) {
            logger.debug("Refreshed tracking for teleported {} at {} in {}",
                    entity.getType().toShortString(), entity.blockPosition(), level.dimension().location());
            return;
        }

        // The entity arrived in a chunk that isn't loaded yet, so Vanilla has already untracked it and will only
        // track it again once that chunk becomes accessible. Adding a tracker now would make Vanilla throw at that
        // point, so wait for the chunk instead.
        logger.debug("Deferring tracking refresh for teleported {} at {} in {}, destination chunk is not loaded yet",
                entity.getType().toShortString(), entity.blockPosition(), level.dimension().location());
        pendingRefreshes.add(new PendingRefresh(entity, level.getServer().getTickCount()));
    }

    private static void processPendingRefreshes(ServerLevel level) {
        if (pendingRefreshes.isEmpty()) {
            return;
        }

        final var tickCount = level.getServer().getTickCount();
        pendingRefreshes.removeIf(pending -> {
            final var entity = pending.entity();
            if (entity.isRemoved()) {
                return true;
            }

            // Entities that ended up in another dimension are handled by the tick of that level.
            if (entity.level() == level && tryRefresh(entity, level)) {
                logger.debug("Refreshed tracking for teleported {} at {} in {} after waiting {} ticks",
                        entity.getType().toShortString(),
                        entity.blockPosition(),
                        level.dimension().location(),
                        tickCount - pending.queuedAtTick());
                return true;
            }

            if (tickCount - pending.queuedAtTick() > MAX_PENDING_TICKS) {
                logger.warn("Gave up refreshing tracking for teleported {} at {}, it never became tracked. It may be invisible until the area is reloaded.",
                        entity.getType().toShortString(), entity.blockPosition());
                return true;
            }

            return false;
        });
    }

    private static boolean tryRefresh(Entity entity, ServerLevel level) {
        final var chunkSource = level.getChunkSource();
        if (!((ChunkMapAccessor) chunkSource.chunkMap).getEntityMap().containsKey(entity.getId())) {
            return false;
        }

        chunkSource.removeEntity(entity);
        chunkSource.addEntity(entity);
        return true;
    }
}
