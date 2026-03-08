package net.blay09.mods.waystones.store;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class InMemoryWaystonesPlayerStore implements WaystonesPlayerStore {
    private final List<UUID> sortingIndex = new ArrayList<>();
    private final Map<UUID, Waystone> waystones = new HashMap<>();

    @Override
    public void activateWaystone(Player player, Waystone waystone) {
        waystones.put(waystone.getWaystoneUid(), waystone);
        sortingIndex.add(waystone.getWaystoneUid());
    }

    @Override
    public boolean isWaystoneActivated(Player player, Waystone waystone) {
        return waystones.containsKey(waystone.getWaystoneUid());
    }

    @Override
    public void deactivateWaystone(Player player, Waystone waystone) {
        waystones.remove(waystone.getWaystoneUid());
        sortingIndex.remove(waystone.getWaystoneUid());
    }

    @Override
    public Collection<Waystone> getWaystones(Player player) {
        return waystones.values();
    }

    @Override
    public void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        sortingIndex.remove(waystoneUid);
        sortingIndex.add(0, waystoneUid);
    }

    @Override
    public void sortWaystoneAsLast(Player player, UUID waystoneUid) {
        sortingIndex.remove(waystoneUid);
        sortingIndex.add(waystoneUid);
    }

    @Override
    public void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid) {
        final var waystoneIndex = sortingIndex.indexOf(waystoneUid);
        final var otherWaystoneIndex = sortingIndex.indexOf(otherWaystoneUid);
        if (waystoneIndex != -1 && otherWaystoneIndex != -1) {
            Collections.swap(sortingIndex, waystoneIndex, otherWaystoneIndex);
        }
    }

    @Override
    public List<UUID> getSortingIndex(Player player) {
        return sortingIndex;
    }

    @Override
    public List<UUID> ensureSortingIndex(Player player, Collection<Waystone> waystones) {
        final var existing = new HashSet<>(sortingIndex);

        for (final var waystone : waystones) {
            final var waystoneUid = waystone.getWaystoneUid();
            if (!existing.contains(waystoneUid)) {
                sortingIndex.add(waystoneUid);
            }
        }

        return sortingIndex;
    }

    @Override
    public void setSortingIndex(Player player, List<UUID> sortingIndex) {
        this.sortingIndex.clear();
        this.sortingIndex.addAll(sortingIndex);
    }

    public void setWaystones(Collection<Waystone> waystones) {
        this.waystones.clear();
        for (final var waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
        }
    }
}
