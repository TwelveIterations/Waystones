package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Comparator;

public class PreferSameDimensionComparator implements Comparator<Waystone> {

    private final ResourceKey<Level> dimensionId;

    public PreferSameDimensionComparator(ResourceKey<Level> dimensionId) {
        this.dimensionId = dimensionId;
    }

    @Override
    public int compare(Waystone o1, Waystone o2) {
        if (o1.getDimension().equals(dimensionId) && !o2.getDimension().equals(dimensionId)) {
            return -1;
        } else if (!o1.getDimension().equals(dimensionId) && o2.getDimension().equals(dimensionId)) {
            return 1;
        }

        return 0;
    }
}
