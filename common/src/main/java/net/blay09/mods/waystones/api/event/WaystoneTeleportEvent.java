package net.blay09.mods.waystones.api.event;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class WaystoneTeleportEvent extends BalmEvent {

    public static class Pre extends WaystoneTeleportEvent {
        private final WaystoneTeleportContext context;

        public Pre(WaystoneTeleportContext context) {
            this.context = context;
        }

        public WaystoneTeleportContext getContext() {
            return context;
        }

        public WarpRequirement getRequirements() {
            return context.getRequirements();
        }

        public void setRequirements(WarpRequirement warpRequirement) {
            context.setRequirements(warpRequirement);
        }

        public void addAdditionalEntity(Entity additionalEntity) {
            context.addAdditionalEntity(additionalEntity);
        }
    }

    public static class Prepare extends WaystoneTeleportEvent {
        private final WaystoneTeleportContext context;
        private final Set<ChunkPos> chunkPositions;
        private final List<Function<Either<Void, WaystoneTeleportError>, CompletableFuture<Either<Void, WaystoneTeleportError>>>> preparationTasks = new ArrayList<>();

        public Prepare(WaystoneTeleportContext context, Set<ChunkPos> chunkPositions) {
            this.context = context;
            this.chunkPositions = chunkPositions;
        }

        /**
         * The context that is being prepared for teleport. Requirements have not been consumed yet.
         */
        public WaystoneTeleportContext getContext() {
            return context;
        }

        /**
         * The chunk positions that will be loaded before the teleport continues. Changes made during this event are respected.
         */
        public Set<ChunkPos> getChunkPositions() {
            return chunkPositions;
        }

        public void addChunkPosition(ChunkPos chunkPos) {
            chunkPositions.add(chunkPos);
        }

        /**
         * Registers additional asynchronous preparation work that must complete before the teleport continues.
         */
        public void addPreparationTask(Function<Either<Void, WaystoneTeleportError>, CompletableFuture<Either<Void, WaystoneTeleportError>>> task) {
            preparationTasks.add(Objects.requireNonNull(task));
        }

        public List<Function<Either<Void, WaystoneTeleportError>, CompletableFuture<Either<Void, WaystoneTeleportError>>>> getPreparationTasks() {
            return Collections.unmodifiableList(preparationTasks);
        }
    }

    public static class Post extends WaystoneTeleportEvent {
        private final WaystoneTeleportContext context;
        private final List<Entity> teleportedEntities;

        public Post(WaystoneTeleportContext context, List<Entity> teleportedEntities) {
            this.context = context;
            this.teleportedEntities = teleportedEntities;
        }

        /**
         * The context that was used for this teleport. Changes made at this point are ignored.
         */
        public WaystoneTeleportContext getContext() {
            return context;
        }

        public List<Entity> getTeleportedEntities() {
            return teleportedEntities;
        }
    }

}
