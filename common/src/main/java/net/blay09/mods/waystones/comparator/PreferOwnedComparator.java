package net.blay09.mods.waystones.comparator;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class PreferOwnedComparator implements Comparator<IWaystone> {

    private final Player owner;

    public PreferOwnedComparator(Player owner) {
        this.owner = owner;
    }

    @Override
    public int compare(IWaystone o1, IWaystone o2) {
        if (o1.isOwner(owner) && !o2.isOwner(owner)) {
            return -1;
        } else if (!o1.isOwner(owner) && o2.isOwner(owner)) {
            return 1;
        }

        return 0;
    }
}
