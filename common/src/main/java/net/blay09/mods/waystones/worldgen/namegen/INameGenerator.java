package net.blay09.mods.waystones.worldgen.namegen;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public interface INameGenerator {
    @Nullable
    String randomName(RandomSource rand);
}