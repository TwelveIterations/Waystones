package net.blay09.mods.waystones.store;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class InMemoryWaystonesPlayerStore implements WaystonesPlayerStore {
    private final List<UUID> sortingIndex = new ArrayList<>();
    private final Map<UUID, Waystone> waystones = new HashMap<>();
    private final Map<UUID, Component> aliases = new HashMap<>();
    private final SetMultimap<UUID, Identifier> waystoneToConfiguredGroups = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Map<Identifier, WaystoneGroup> groupRegistry = new LinkedHashMap<>();

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
    public Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid) {
        return Optional.ofNullable(aliases.get(waystoneUid));
    }

    @Override
    public void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias) {
        if (alias != null) {
            aliases.put(waystoneUid, alias);
        } else {
            aliases.remove(waystoneUid);
        }
    }

    @Override
    public Collection<WaystoneGroup> getWaystoneGroupRegistry(Player player) {
        return sortedGroups(groupRegistry.values());
    }

    @Override
    public void setWaystoneGroupRegistry(Player player, Collection<WaystoneGroup> groups) {
        groupRegistry.clear();
        for (final var group : normalizeGroupSortIndices(groups)) {
            groupRegistry.put(group.identifier(), group);
        }
    }

    @Override
    public void addWaystoneGroups(Player player, Collection<WaystoneGroup> groups) {
        for (final var group : groups) {
            final var existingGroup = groupRegistry.get(group.identifier());
            if (existingGroup == null || group.inbuilt() && !existingGroup.inbuilt()) {
                final int sortIndex = existingGroup != null ? existingGroup.sortIndex() : groupRegistry.size();
                groupRegistry.put(group.identifier(), withSortIndex(group, sortIndex));
            }
        }
    }

    @Override
    public void sortWaystoneGroupAsFirst(Player player, Identifier groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        final var group = removeGroup(groups, groupId);
        if (group != null) {
            groups.addFirst(group);
            setWaystoneGroupRegistry(player, groups);
        }
    }

    @Override
    public void sortWaystoneGroupAsLast(Player player, Identifier groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        final var group = removeGroup(groups, groupId);
        if (group != null) {
            groups.add(group);
            setWaystoneGroupRegistry(player, groups);
        }
    }

    @Override
    public void sortWaystoneGroupSwap(Player player, Identifier groupId, Identifier otherGroupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        final var groupIndex = indexOfGroup(groups, groupId);
        final var otherGroupIndex = indexOfGroup(groups, otherGroupId);
        if (groupIndex != -1 && otherGroupIndex != -1) {
            Collections.swap(groups, groupIndex, otherGroupIndex);
            setWaystoneGroupRegistry(player, groups);
        }
    }

    @Override
    public Set<Identifier> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        return waystoneToConfiguredGroups.get(waystoneUid);
    }

    @Override
    public void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<Identifier> configuredGroups) {
        waystoneToConfiguredGroups.replaceValues(waystoneUid, configuredGroups);
    }

    @Override
    public void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        sortingIndex.remove(waystoneUid);
        sortingIndex.addFirst(waystoneUid);
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
    public List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones) {
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

    public void setWaystones(Player player, Collection<Waystone> waystones) {
        this.waystones.clear();
        aliases.clear();
        waystoneToConfiguredGroups.clear();
        for (final var waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
            if (waystone instanceof UserDecoratedWaystone userDecoratedWaystone) {
                userDecoratedWaystone.getAlias().ifPresent(alias -> setWaystoneAlias(player, waystone.getWaystoneUid(), alias));
                setConfiguredWaystoneGroups(player, userDecoratedWaystone.getWaystoneUid(), userDecoratedWaystone.getConfiguredGroups());
            }
        }
    }

    private static List<WaystoneGroup> sortedGroups(Collection<WaystoneGroup> groups) {
        return groups.stream()
                .sorted(Comparator.comparingInt(WaystoneGroup::sortIndex)
                        .thenComparing(group -> group.name().getString(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(group -> group.identifier().toString()))
                .toList();
    }

    private static List<WaystoneGroup> normalizeGroupSortIndices(Collection<WaystoneGroup> groups) {
        final var result = new ArrayList<WaystoneGroup>();
        final var existing = new HashSet<Identifier>();
        for (final var group : groups) {
            if (existing.add(group.identifier())) {
                result.add(withSortIndex(group, result.size()));
            }
        }
        return result;
    }

    private static WaystoneGroup withSortIndex(WaystoneGroup group, int sortIndex) {
        return new WaystoneGroupImpl(group.identifier(), group.name(), group.icon(), group.color(), group.inbuilt(), group.hidden(), sortIndex);
    }

    private static @Nullable WaystoneGroup removeGroup(List<WaystoneGroup> groups, Identifier groupId) {
        final int index = indexOfGroup(groups, groupId);
        return index != -1 ? groups.remove(index) : null;
    }

    private static int indexOfGroup(List<WaystoneGroup> groups, Identifier groupId) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).identifier().equals(groupId)) {
                return i;
            }
        }
        return -1;
    }

}
