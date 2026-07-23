package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class PreferOwnedComparator implements Comparator<Waystone> {

    private final Player owner;

    public PreferOwnedComparator(Player owner) {
        this.owner = owner;
    }

    @Override
    public int compare(Waystone o1, Waystone o2) {
        if (o1.isOwner(owner) && !o2.isOwner(owner)) {
            return -1;
        } else if (!o1.isOwner(owner) && o2.isOwner(owner)) {
            return 1;
        }

        return 0;
    }
}
