package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class DistanceToPlayerComparator implements Comparator<Waystone> {

    private final Player player;

    public DistanceToPlayerComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(Waystone o1, Waystone o2) {
        double distance1 = o1.getPos().distSqr(player.blockPosition());
        double distance2 = o2.getPos().distSqr(player.blockPosition());
        return Double.compare(distance1, distance2);
    }
}
