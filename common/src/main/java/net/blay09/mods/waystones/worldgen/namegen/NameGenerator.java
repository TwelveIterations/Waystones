package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public interface NameGenerator {
    Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand);
}