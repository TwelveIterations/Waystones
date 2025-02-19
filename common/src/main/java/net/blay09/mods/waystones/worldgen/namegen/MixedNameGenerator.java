package net.blay09.mods.waystones.worldgen.namegen;

import net.minecraft.util.RandomSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MixedNameGenerator implements INameGenerator {

    private final List<INameGenerator> nameGenerators;

    public MixedNameGenerator(INameGenerator... nameGenerators) {
        this.nameGenerators = Arrays.asList(nameGenerators);
    }

    @Override
    public String randomName(RandomSource rand) {
        Collections.shuffle(nameGenerators);
        for (INameGenerator nameGenerator : nameGenerators) {
            String name = nameGenerator.randomName(rand);
            if (name != null) {
                return name;
            }
        }

        return null;
    }
}
