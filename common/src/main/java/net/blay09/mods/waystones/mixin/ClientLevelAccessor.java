package net.blay09.mods.waystones.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public interface ClientLevelAccessor {

    @Accessor("tickingEntities")
    EntityTickList waystones$getTickingEntities();
}
