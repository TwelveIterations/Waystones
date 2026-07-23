package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MixedNameGenerator implements NameGenerator {

    private final List<NameGenerator> nameGenerators;

    public MixedNameGenerator(NameGenerator... nameGenerators) {
        this.nameGenerators = Arrays.asList(nameGenerators);
    }

    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        Collections.shuffle(nameGenerators);
        for (NameGenerator nameGenerator : nameGenerators) {
            final var name = nameGenerator.generateName(level, waystone, rand);
            if (name.isPresent()) {
                return name;
            }
        }

        return Optional.empty();
    }
}
