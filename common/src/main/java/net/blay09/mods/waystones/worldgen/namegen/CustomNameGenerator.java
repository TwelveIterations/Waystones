package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;

public class CustomNameGenerator implements NameGenerator {

    private final boolean allowDuplicates;
    private final Set<String> usedNames;

    public CustomNameGenerator(boolean allowDuplicates, Set<String> usedNames) {
        this.allowDuplicates = allowDuplicates;
        this.usedNames = usedNames;
    }

    @Override
    public Optional<Component> generateName(LevelAccessor level, Waystone waystone, RandomSource rand) {
        final var customNames = new ArrayList<>(WaystonesConfig.getActive().worldGen.nameGenerationPresets);
        Collections.shuffle(customNames);
        for (final var customName : customNames) {
            if (allowDuplicates || !usedNames.contains(customName)) {
                return Optional.of(Component.literal(customName));
            }
        }

        return Optional.empty();
    }
}
