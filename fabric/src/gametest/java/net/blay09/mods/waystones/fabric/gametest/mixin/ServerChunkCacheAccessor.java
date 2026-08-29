package net.blay09.mods.waystones.fabric.gametest.mixin;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheAccessor {

    @Accessor
    TicketStorage getTicketStorage();

    @Invoker
    boolean callRunDistanceManagerUpdates();
}
