package net.blay09.mods.waystones.api.error;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class WaystoneTeleportError {

    private final Component component;

    public WaystoneTeleportError() {
        this.component = Component.empty();
    }

    public WaystoneTeleportError(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    /**
     * @deprecated This is more of an unexpected state error caused by bad code than an actual logical runtime error. No longer used by Waystones.
     */
    @Deprecated
    public static class NotOnServer extends WaystoneTeleportError {
    }

    public static class InvalidDimension extends WaystoneTeleportError {
        private final ResourceKey<Level> dimension;

        public InvalidDimension(ResourceKey<Level> dimension) {
            this.dimension = dimension;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }
    }

    public static class InvalidWaystone extends WaystoneTeleportError {
        private final Waystone waystone;

        public InvalidWaystone(Waystone waystone) {
            this.waystone = waystone;
        }

        public Waystone getWaystone() {
            return waystone;
        }
    }

    public static class MissingWaystone extends WaystoneTeleportError {
        private final Waystone waystone;

        public MissingWaystone(Waystone waystone) {
            super(Component.translatable("chat.waystones.waystone_missing"));
            this.waystone = waystone;
        }

        public Waystone getWaystone() {
            return waystone;
        }
    }

    public static class CancelledByEvent extends WaystoneTeleportError {
    }

    public static class DimensionalWarpDenied extends WaystoneTeleportError {
        public DimensionalWarpDenied() {
            super(Component.translatable("chat.waystones.cannot_dimension_warp"));
        }
    }

    public static class LeashedWarpDenied extends WaystoneTeleportError {
        public LeashedWarpDenied() {
            super(Component.translatable("chat.waystones.cannot_transport_leashed"));
        }
    }

    public static class SpecificLeashedWarpDenied extends WaystoneTeleportError {
        private final Entity entity;

        public SpecificLeashedWarpDenied(Entity entity) {
            super(Component.translatable("chat.waystones.cannot_transport_this_leashed"));
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }
    }

    public static class LeashedDimensionalWarpDenied extends WaystoneTeleportError {
        public LeashedDimensionalWarpDenied() {
            super(Component.translatable("chat.waystones.cannot_transport_leashed_dimensional"));
        }
    }

    public static class RequirementsNotMet extends WaystoneTeleportError {
        public RequirementsNotMet() {
            super(Component.translatable("chat.waystones.requirements_not_met"));
        }
    }

    public static class TeleportNoLongerValid extends WaystoneTeleportError {
        public TeleportNoLongerValid() {
            super(Component.translatable("chat.waystones.teleport_no_longer_valid"));
        }
    }

    public static class SourceItemMissing extends WaystoneTeleportError {
        public SourceItemMissing() {
            super(Component.translatable("chat.waystones.source_item_missing"));
        }
    }

    public static class SourceWaystoneOutOfRange extends WaystoneTeleportError {
        public SourceWaystoneOutOfRange() {
            super(Component.translatable("chat.waystones.source_waystone_out_of_range"));
        }
    }

    public static class TeleportFailed extends WaystoneTeleportError {
        public TeleportFailed() {
            super(Component.translatable("chat.waystones.teleport_failed"));
        }
    }

    public static class DestinationChunkLoadFailed extends WaystoneTeleportError {
        private final ResourceKey<Level> dimension;
        private final ChunkPos chunkPos;
        private final String reason;

        public DestinationChunkLoadFailed(ResourceKey<Level> dimension, ChunkPos chunkPos, String reason) {
            super(Component.translatable("chat.waystones.destination_chunk_load_failed"));
            this.dimension = dimension;
            this.chunkPos = chunkPos;
            this.reason = reason;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        public ChunkPos getChunkPos() {
            return chunkPos;
        }

        public String getReason() {
            return reason;
        }
    }
}
