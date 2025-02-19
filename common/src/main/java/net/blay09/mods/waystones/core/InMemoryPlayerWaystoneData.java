package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class InMemoryPlayerWaystoneData implements IPlayerWaystoneData {
    private final List<IWaystone> sortedWaystones = new ArrayList<>();
    private final Map<UUID, IWaystone> waystones = new HashMap<>();
    private long warpStoneCooldownUntil;
    private long inventoryButtonCooldownUntil;

    @Override
    public void activateWaystone(Player player, IWaystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        sortedWaystones.add(waystone);
    }

    @Override
    public boolean isWaystoneActivated(Player player, IWaystone waystone) {
        return waystones.containsKey(waystone.getWaystoneUid());
    }

    @Override
    public void deactivateWaystone(Player player, IWaystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        sortedWaystones.remove(waystone);
    }

    @Override
    public long getWarpStoneCooldownUntil(Player player) {
        return warpStoneCooldownUntil;
    }

    @Override
    public void setWarpStoneCooldownUntil(Player player, long timeStamp) {
        warpStoneCooldownUntil = timeStamp;
    }

    @Override
    public long getInventoryButtonCooldownUntil(Player player) {
        return inventoryButtonCooldownUntil;
    }

    @Override
    public void setInventoryButtonCooldownUntil(Player player, long timeStamp) {
        inventoryButtonCooldownUntil = timeStamp;
    }

    @Override
    public List<IWaystone> getWaystones(Player player) {
        return sortedWaystones;
    }

    @Override
    public void swapWaystoneSorting(Player player, int index, int otherIndex) {
        if (otherIndex == -1) {
            IWaystone waystone = sortedWaystones.remove(index);
            sortedWaystones.add(0, waystone);
        } else if (otherIndex == sortedWaystones.size()) {
            IWaystone waystone = sortedWaystones.remove(index);
            sortedWaystones.add(waystone);
        } else {
            Collections.swap(sortedWaystones, index, otherIndex);
        }
    }

    public void setWaystones(List<IWaystone> waystones) {
        this.sortedWaystones.clear();
        this.waystones.clear();
        this.sortedWaystones.addAll(waystones);
        for (IWaystone waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }
}
