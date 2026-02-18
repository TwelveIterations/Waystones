package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Comparator;

public class PreferSameDimensionComparator implements Comparator<IWaystone> {

    private final ResourceKey<Level> dimensionId;

    public PreferSameDimensionComparator(ResourceKey<Level> dimensionId) {
        this.dimensionId = dimensionId;
    }

    @Override
    public int compare(IWaystone o1, IWaystone o2) {
        if (o1.getDimension().equals(dimensionId) && !o2.getDimension().equals(dimensionId)) {
            return -1;
        } else if (!o1.getDimension().equals(dimensionId) && o2.getDimension().equals(dimensionId)) {
            return 1;
        }

        return 0;
    }
}
