package net.blay09.mods.waystones.core;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.blay09.mods.waystones.WaystoneSortMode;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InMemoryPlayerWaystoneData implements IPlayerWaystoneData {
    private final List<UUID> sortingIndex = new ArrayList<>();
    private final Map<UUID, Waystone> waystones = new HashMap<>();
    private final Map<ResourceLocation, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Component> aliases = new HashMap<>();
    private final SetMultimap<UUID, ResourceLocation> waystoneToConfiguredGroups = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Map<ResourceLocation, WaystoneGroup> groupRegistry = new LinkedHashMap<>();
    private WaystoneSortMode waystoneSortMode = WaystoneSortMode.MANUAL;

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
    public Map<ResourceLocation, Long> getCooldowns(Player player) {
        return cooldowns;
    }

    @Override
    public void resetCooldowns(Player player) {
        cooldowns.clear();
    }

    @Override
    public long getCooldownUntil(Player player, ResourceLocation key) {
        return cooldowns.getOrDefault(key, 0L);
    }

    @Override
    public void setCooldownUntil(Player player, ResourceLocation key, long timeStamp) {
        cooldowns.put(key, timeStamp);
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
        return WaystoneGroups.sorted(groupRegistry.values());
    }

    @Override
    public void setWaystoneGroupRegistry(Player player, Collection<WaystoneGroup> groups) {
        groupRegistry.clear();
        for (final var group : WaystoneGroups.normalizeSortIndices(groups)) {
            groupRegistry.put(group.identifier(), group);
        }
    }

    @Override
    public void addWaystoneGroups(Player player, Collection<WaystoneGroup> groups) {
        for (final var group : groups) {
            final var existingGroup = groupRegistry.get(group.identifier());
            if (existingGroup == null || group.inbuilt() && !existingGroup.inbuilt()) {
                final int sortIndex = existingGroup != null ? existingGroup.sortIndex() : groupRegistry.size();
                groupRegistry.put(group.identifier(), WaystoneGroups.withSortIndex(group, sortIndex));
            }
        }
    }

    @Override
    public void sortWaystoneGroupAsFirst(Player player, ResourceLocation groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        WaystoneGroups.removeGroup(groups, groupId).ifPresent(group -> {
            groups.addFirst(group);
            setWaystoneGroupRegistry(player, groups);
        });
    }

    @Override
    public void sortWaystoneGroupAsLast(Player player, ResourceLocation groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        WaystoneGroups.removeGroup(groups, groupId).ifPresent(group -> {
            groups.add(group);
            setWaystoneGroupRegistry(player, groups);
        });
    }

    @Override
    public void sortWaystoneGroupSwap(Player player, ResourceLocation groupId, ResourceLocation otherGroupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        final var groupIndex = WaystoneGroups.indexOfGroup(groups, groupId);
        final var otherGroupIndex = WaystoneGroups.indexOfGroup(groups, otherGroupId);
        if (groupIndex != -1 && otherGroupIndex != -1) {
            Collections.swap(groups, groupIndex, otherGroupIndex);
            setWaystoneGroupRegistry(player, groups);
        }
    }

    @Override
    public Set<ResourceLocation> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        return waystoneToConfiguredGroups.get(waystoneUid);
    }

    @Override
    public void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<ResourceLocation> configuredGroups) {
        waystoneToConfiguredGroups.replaceValues(waystoneUid, configuredGroups);
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

    @Override
    public WaystoneSortMode getWaystoneSortMode(Player player) {
        return waystoneSortMode;
    }

    @Override
    public void setWaystoneSortMode(Player player, WaystoneSortMode sortMode) {
        waystoneSortMode = sortMode;
    }

    public void setWaystones(Player player, Collection<? extends Waystone> waystones) {
        this.waystones.clear();
        aliases.clear();
        waystoneToConfiguredGroups.clear();
        for (final var waystone : waystones) {
            this.waystones.put(waystone.getWaystoneUid(), waystone);
            if (waystone instanceof PersonalizedWaystoneImpl personalizedWaystone) {
                personalizedWaystone.getAlias().ifPresent(alias -> setWaystoneAlias(player, waystone.getWaystoneUid(), alias));
                setConfiguredWaystoneGroups(player, personalizedWaystone.getWaystoneUid(), personalizedWaystone.getConfiguredGroups());
            }
        }
    }

    @Override
    public Optional<Waystone> findWaystoneByName(Player player, String name) {
        return this.waystones.values().stream().filter(it -> it.getEffectiveName().getString().equals(name)).findFirst();
    }
}
