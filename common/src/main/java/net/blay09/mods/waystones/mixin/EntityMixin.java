package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.core.WaystoneTeleportedEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements WaystoneTeleportedEntity {

    @Unique
    private boolean waystones$teleportedByWaystone;

    @Override
    public void waystones$markTeleportedByWaystone() {
        waystones$teleportedByWaystone = true;
    }

    @Override
    public boolean waystones$consumeTeleportedByWaystone() {
        if (waystones$teleportedByWaystone) {
            waystones$teleportedByWaystone = false;
            return true;
        }

        return false;
    }
}
