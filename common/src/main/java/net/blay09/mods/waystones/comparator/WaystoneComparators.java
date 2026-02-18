package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class WaystoneComparators {
    public static Comparator<IWaystone> forAdminInspection(Player caller, Player target) {
        return new PreferOwnedComparator(target)
                .thenComparing(new PreferSameDimensionComparator(caller.level().dimension())
                        .thenComparing(new DistanceToPlayerComparator(caller)));
    }
}
