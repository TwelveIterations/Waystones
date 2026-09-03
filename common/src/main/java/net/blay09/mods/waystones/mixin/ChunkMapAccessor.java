package net.blay09.mods.waystones.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
    /**
     * The trackers keyed by entity id. Its value type is a private inner class of {@link ChunkMap}, so it can only
     * be used to check whether an entity is being tracked at all.
     */
    @Accessor("entityMap")
    Int2ObjectMap<?> getEntityMap();
}
