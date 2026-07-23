package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public class SequencedNameGenerator implements NameGenerator {

    private final NameGenerator[] nameGenerators;

    public SequencedNameGenerator(NameGenerator... nameGenerators) {
        this.nameGenerators = nameGenerators;
    }

    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        for (final var nameGenerator : nameGenerators) {
            final var name = nameGenerator.generateName(level, waystone, rand);
            if (name.isPresent()) {
                return name;
            }
        }
        return Optional.empty();
    }
}
