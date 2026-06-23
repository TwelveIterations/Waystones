package net.blay09.mods.waystones.store;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaystonesPlayerStore {
    void activateWaystone(Player player, Waystone waystone);
    boolean isWaystoneActivated(Player player, Waystone waystone);
    void deactivateWaystone(Player player, Waystone waystone);
    List<UUID> getSortingIndex(Player player);
    List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones);
    void setSortingIndex(Player player, List<UUID> sortingIndex);
    Collection<Waystone> getWaystones(Player player);
    Optional<String> getWaystoneAlias(Player player, UUID waystoneUid);
    void setWaystoneAlias(Player player, UUID waystoneUid, String alias);
    void sortWaystoneAsFirst(Player player, UUID waystoneUid);
    void sortWaystoneAsLast(Player player, UUID waystoneUid);
    void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid);
}
