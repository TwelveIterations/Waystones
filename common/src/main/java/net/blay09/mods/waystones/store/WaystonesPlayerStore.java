package net.blay09.mods.waystones.store;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.*;

public interface WaystonesPlayerStore {
    void activateWaystone(Player player, Waystone waystone);
    boolean isWaystoneActivated(Player player, Waystone waystone);
    void deactivateWaystone(Player player, Waystone waystone);
    List<UUID> getSortingIndex(Player player);
    List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones);
    void setSortingIndex(Player player, List<UUID> sortingIndex);
    Collection<Waystone> getWaystones(Player player);
    Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid);
    void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias);
    Collection<WaystoneGroup> getWaystoneGroupRegistry(Player player);
    void setWaystoneGroupRegistry(Player player, Collection<WaystoneGroup> groups);
    void addWaystoneGroups(Player player, Collection<WaystoneGroup> groups);
    void sortWaystoneGroupAsFirst(Player player, Identifier groupId);
    void sortWaystoneGroupAsLast(Player player, Identifier groupId);
    void sortWaystoneGroupSwap(Player player, Identifier groupId, Identifier otherGroupId);
    Set<Identifier> getConfiguredWaystoneGroups(Player player, UUID waystoneUid);
    void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<Identifier> groupIds);
    boolean isWaystoneHidden(Player player, UUID waystoneUid);
    void setWaystoneHidden(Player player, UUID waystoneUid, boolean hidden);
    void sortWaystoneAsFirst(Player player, UUID waystoneUid);
    void sortWaystoneAsLast(Player player, UUID waystoneUid);
    void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid);
}
