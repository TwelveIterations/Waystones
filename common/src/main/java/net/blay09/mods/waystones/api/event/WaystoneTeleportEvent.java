package net.blay09.mods.waystones.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Consumer;

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

        public WarpRequirement getRequirements() {
            return context.getRequirements();
        }

        public void setRequirements(WarpRequirement warpRequirement) {
            context.setRequirements(warpRequirement);
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

}
