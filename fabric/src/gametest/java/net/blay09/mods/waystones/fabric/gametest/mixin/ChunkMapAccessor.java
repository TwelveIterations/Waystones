package net.blay09.mods.waystones.fabric.gametest.mixin;

import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {

    @Invoker
    void callProcessUnloads(BooleanSupplier hasMoreTime);
}
