package net.blay09.mods.waystones.worldgen.namegen;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.util.RandomSource;

import java.util.*;

public class CustomNameGenerator implements INameGenerator {

    private final boolean allowDuplicates;
    private final Set<String> usedNames;

    public CustomNameGenerator(boolean allowDuplicates, Set<String> usedNames) {
        this.allowDuplicates = allowDuplicates;
        this.usedNames = usedNames;
    }

    @Override
    public String randomName(RandomSource rand) {
        List<String> customNames = WaystonesConfig.getActive().worldGen.customWaystoneNames;
        Collections.shuffle(customNames);
        for (String customName : customNames) {
            if (allowDuplicates || !usedNames.contains(customName)) {
                return customName;
            }
        }

        return null;
    }
}
