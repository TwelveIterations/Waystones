package net.blay09.mods.waystones.api.event;

import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.world.entity.Entity;

import java.util.List;

// TODO
@Deprecated
public abstract class WaystoneTeleportEvent {

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
