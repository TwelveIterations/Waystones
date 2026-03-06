package net.blay09.mods.waystones.api.error;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
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
}
