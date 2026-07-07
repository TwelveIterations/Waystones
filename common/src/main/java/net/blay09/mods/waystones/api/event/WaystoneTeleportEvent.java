package net.blay09.mods.waystones.api.event;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.EntityTeleportResult;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class WaystoneTeleportEvent {

    public static class Before extends WaystoneTeleportEvent {
        public static final BidirectionalEventMapper<Consumer<Before>> EVENT = Balmstrap.createBoundCustomEvent(Before.class);

        private final WaystoneTeleportContext context;
        private boolean canceled;

        public Before(WaystoneTeleportContext context) {
            this.context = context;
        }

        public WaystoneTeleportContext getContext() {
            return context;
        }

        public void addAdditionalEntity(Entity additionalEntity) {
            context.addAdditionalEntity(additionalEntity);
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public static class Prepare extends WaystoneTeleportEvent {
        public static final BidirectionalEventMapper<Consumer<Prepare>> EVENT = Balmstrap.createBoundCustomEvent(Prepare.class);

        private final WaystoneTeleportContext context;
        private final Set<ChunkPos> chunkPositions;
        private final List<Function<Either<@Nullable Void, WaystoneTeleportError>, CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>>>> preparationTasks = new ArrayList<>();

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
        public void addPreparationTask(Function<Either<@Nullable Void, WaystoneTeleportError>, CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>>> task) {
            preparationTasks.add(Objects.requireNonNull(task));
        }

        public List<Function<Either<@Nullable Void, WaystoneTeleportError>, CompletableFuture<Either<@Nullable Void, WaystoneTeleportError>>>> getPreparationTasks() {
            return Collections.unmodifiableList(preparationTasks);
        }
    }

    public static class After extends WaystoneTeleportEvent {
        public static final BidirectionalEventMapper<Consumer<After>> EVENT = Balmstrap.createBoundCustomEvent(After.class);

        private final WaystoneTeleportContext context;
        private final List<Entity> teleportedEntities;

        public After(WaystoneTeleportContext context, List<Entity> teleportedEntities) {
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

    public static class Complete extends WaystoneTeleportEvent {
        public static final BidirectionalEventMapper<Consumer<Complete>> EVENT = Balmstrap.createBoundCustomEvent(Complete.class);

        private final WaystoneTeleportContext context;
        private final @Nullable EntityTeleportResult primaryResult;
        private final List<EntityTeleportResult> additionalResults;
        private final @Nullable WaystoneTeleportError teleportError;

        public Complete(WaystoneTeleportContext context, @Nullable EntityTeleportResult primaryResult, List<EntityTeleportResult> additionalResults, @Nullable WaystoneTeleportError teleportError) {
            this.context = context;
            this.primaryResult = primaryResult;
            this.additionalResults = additionalResults;
            this.teleportError = teleportError;
        }

        /**
         * The context that was used for this teleport. Changes made at this point are ignored.
         */
        public WaystoneTeleportContext getContext() {
            return context;
        }

        public Optional<EntityTeleportResult> getPrimaryResult() {
            return Optional.ofNullable(primaryResult);
        }

        public List<EntityTeleportResult> getAdditionalResults() {
            return additionalResults;
        }

        public List<EntityTeleportResult> getResults() {
            final var results = new ArrayList<EntityTeleportResult>(additionalResults.size() + 1);
            if (primaryResult != null) {
                results.add(primaryResult);
            }

            results.addAll(additionalResults);
            return results;
        }

        public List<Entity> getTeleportedEntities() {
            return getResults().stream()
                    .filter(EntityTeleportResult::isSuccessful)
                    .map(EntityTeleportResult::entity)
                    .toList();
        }

        public Optional<WaystoneTeleportError> getTeleportError() {
            return Optional.ofNullable(teleportError);
        }
    }

}
