package net.blay09.mods.waystones.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WaystoneTeleportError {

    private final String translationKey;

    public WaystoneTeleportError() {
        this.translationKey = null;
    }

    public WaystoneTeleportError(String translationKey) {
        this.translationKey = translationKey;
    }

    @Nullable
    public String getTranslationKey() {
        return translationKey;
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
        private final IWaystone waystone;

        public InvalidWaystone(IWaystone waystone) {
            this.waystone = waystone;
        }

        public IWaystone getWaystone() {
            return waystone;
        }
    }

    public static class MissingWaystone extends WaystoneTeleportError {
        private final IWaystone waystone;

        public MissingWaystone(IWaystone waystone) {
            super("chat.waystones.waystone_missing");
            this.waystone = waystone;
        }

        public IWaystone getWaystone() {
            return waystone;
        }
    }

    public static class CancelledByEvent extends WaystoneTeleportError {
    }

    public static class WarpModeRejected extends WaystoneTeleportError {
    }

    public static class DimensionalWarpDenied extends WaystoneTeleportError {
        public DimensionalWarpDenied() {
            super("chat.waystones.cannot_dimension_warp");
        }
    }

    public static class LeashedWarpDenied extends WaystoneTeleportError {
        public LeashedWarpDenied() {
            super("chat.waystones.cannot_transport_leashed");
        }
    }

    public static class SpecificLeashedWarpDenied extends WaystoneTeleportError {
        public SpecificLeashedWarpDenied() {
            super("chat.waystones.cannot_transport_this_leashed");
        }
    }

    public static class LeashedDimensionalWarpDenied extends WaystoneTeleportError {
        public LeashedDimensionalWarpDenied() {
            super("chat.waystones.cannot_transport_leashed_dimensional");
        }
    }

    public static class NotEnoughXp extends WaystoneTeleportError {
    }
}
