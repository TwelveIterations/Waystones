
package net.blay09.mods.waystones.store;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PersistentWaystonesPlayerStore implements WaystonesPlayerStore {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String SORTING_INDEX = "SortingIndex";
    private static final String ALIASES = "Aliases";
    private static final String GROUPS = "Groups";
    private static final String GROUP_REGISTRY = "GroupRegistry";
    private static final String GROUP_ID = "Id";
    private static final String GROUP_NAME = "Name";
    private static final String GROUP_ICON = "Icon";
    private static final String GROUP_COLOR = "Color";
    private static final String GROUP_INBUILT = "Inbuilt";
    private static final String GROUP_HIDDEN = "Hidden";
    private static final String GROUP_SORT_INDEX = "SortIndex";

    @Override
    public void activateWaystone(Player player, Waystone waystone) {
        ListTag activatedWaystonesData = getActivatedWaystonesData(getWaystonesData(player));
        activatedWaystonesData.add(StringTag.valueOf(waystone.getWaystoneUid().toString()));
    }

    @Override
    public boolean isWaystoneActivated(Player player, Waystone waystone) {
        ListTag activatedWaystones = getActivatedWaystonesData(getWaystonesData(player));
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (Tag activatedWaystone : activatedWaystones) {
            if (activatedWaystone.asString().map(waystoneUid::equals).orElse(false)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Waystone> getWaystones(Player player) {
        final var activatedWaystonesTag = getActivatedWaystonesData(getWaystonesData(player));
        final var waystones = new ArrayList<Waystone>();
        for (final var iterator = activatedWaystonesTag.iterator(); iterator.hasNext(); ) {
            final var activatedWaystoneTag = iterator.next();
            activatedWaystoneTag.asString().map(UUID::fromString).ifPresentOrElse(waystoneId -> {
                final var proxy = new WaystoneProxy(player.level().getServer(), waystoneId);
                if (proxy.isValid()) {
                    waystones.add(proxy);
                } else {
                    iterator.remove();
                }
            }, iterator::remove);
        }

        return waystones;
    }

    @Override
    public Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid) {
        final var aliases = getAliasesData(getWaystonesData(player));
        final var aliasKey = waystoneUid.toString();
        return aliases.contains(aliasKey) ? readComponent(aliases.get(aliasKey)) : Optional.empty();
    }

    @Override
    public void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias) {
        final var aliases = getAliasesData(getWaystonesData(player));
        if (alias != null) {
            writeComponent(aliases, waystoneUid.toString(), alias);
        } else {
            aliases.remove(waystoneUid.toString());
        }
    }

    @Override
    public Collection<WaystoneGroup> getWaystoneGroupRegistry(Player player) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        final var groups = new ArrayList<WaystoneGroup>();
        final var existing = new HashSet<Identifier>();
        for (final var groupId : groupRegistry.keySet()) {
            final var group = readGroup(groupRegistry.getCompoundOrEmpty(groupId));
            if (group != null && existing.add(group.identifier())) {
                groups.add(group);
            }
        }
        return WaystoneGroups.sorted(groups);
    }

    @Override
    public void setWaystoneGroupRegistry(Player player, Collection<WaystoneGroup> groups) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        for (final var groupId : List.copyOf(groupRegistry.keySet())) {
            groupRegistry.remove(groupId);
        }
        final var normalizedGroups = new LinkedHashMap<Identifier, WaystoneGroup>();
        for (final var group : groups) {
            normalizedGroups.putIfAbsent(group.identifier(), group);
        }
        int sortIndex = 0;
        for (final var group : normalizedGroups.values()) {
            writeGroup(groupRegistry, WaystoneGroups.withSortIndex(group, sortIndex++));
        }
    }

    @Override
    public void addWaystoneGroups(Player player, Collection<WaystoneGroup> groups) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        for (final var group : groups) {
            final var existingGroup = readGroup(groupRegistry.getCompoundOrEmpty(group.identifier().toString()));
            if (existingGroup == null || group.inbuilt() && !existingGroup.inbuilt()) {
                final int sortIndex = existingGroup != null ? existingGroup.sortIndex() : groupRegistry.size();
                writeGroup(groupRegistry, WaystoneGroups.withSortIndex(group, sortIndex));
            }
        }
    }

    @Override
    public void sortWaystoneGroupAsFirst(Player player, Identifier groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        WaystoneGroups.removeGroup(groups, groupId).ifPresent(group -> {
            groups.addFirst(group);
            setWaystoneGroupRegistry(player, groups);
        });
    }

    @Override
    public void sortWaystoneGroupAsLast(Player player, Identifier groupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        WaystoneGroups.removeGroup(groups, groupId).ifPresent(group -> {
            groups.add(group);
            setWaystoneGroupRegistry(player, groups);
        });
    }

    @Override
    public void sortWaystoneGroupSwap(Player player, Identifier groupId, Identifier otherGroupId) {
        final var groups = new ArrayList<>(getWaystoneGroupRegistry(player));
        final var groupIndex = WaystoneGroups.indexOfGroup(groups, groupId);
        final var otherGroupIndex = WaystoneGroups.indexOfGroup(groups, otherGroupId);
        if (groupIndex != -1 && otherGroupIndex != -1) {
            Collections.swap(groups, groupIndex, otherGroupIndex);
            setWaystoneGroupRegistry(player, groups);
        }
    }

    @Override
    public Set<Identifier> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        final var groupsByWaystone = getGroupsData(getWaystonesData(player));
        final var groupsData = groupsByWaystone.getListOrEmpty(waystoneUid.toString());
        final var configuredGroups = new HashSet<Identifier>();
        for (final var groupEntry : groupsData) {
            groupEntry.asString().map(Identifier::tryParse).ifPresent(configuredGroups::add);
        }
        return configuredGroups;
    }

    private static @Nullable WaystoneGroup readGroup(Tag groupEntry) {
        if (groupEntry instanceof CompoundTag groupData) {
            final var id = groupData.getString(GROUP_ID).map(Identifier::tryParse).orElse(null);
            if (id == null) {
                return null;
            }

            final var icon = groupData.getString(GROUP_ICON).map(Identifier::tryParse).orElse(id);
            final var name = readComponent(groupData.get(GROUP_NAME)).orElseGet(() -> Component.literal(id.toString()));
            final var color = groupData.getInt(GROUP_COLOR).orElse(0xFFFFFFFF);
            final var inbuilt = groupData.getBoolean(GROUP_INBUILT).orElse(false);
            final var hidden = groupData.getBoolean(GROUP_HIDDEN).orElse(false);
            final var sortIndex = groupData.getInt(GROUP_SORT_INDEX).orElse(0);
            return new WaystoneGroupImpl(id, name, icon, color, inbuilt, hidden, sortIndex);
        }

        return null;
    }

    @Override
    public void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<Identifier> groupIds) {
        final var groupsByWaystone = getGroupsData(getWaystonesData(player));
        writeGroupIds(groupsByWaystone, waystoneUid, groupIds);
    }

    private static void writeGroupIds(CompoundTag groupsByWaystone, UUID waystoneUid, Collection<Identifier> groupIds) {
        final var normalizedGroupIds = new LinkedHashSet<>(groupIds);
        if (normalizedGroupIds.isEmpty()) {
            groupsByWaystone.remove(waystoneUid.toString());
            return;
        }

        final var groupsData = new ListTag();
        for (final var groupId : normalizedGroupIds) {
            groupsData.add(StringTag.valueOf(groupId.toString()));
        }
        groupsByWaystone.put(waystoneUid.toString(), groupsData);
    }

    private static void writeGroup(CompoundTag groupRegistry, WaystoneGroup group) {
        final var groupData = new CompoundTag();
        groupData.putString(GROUP_ID, group.identifier().toString());
        writeComponent(groupData, GROUP_NAME, group.name());
        groupData.putString(GROUP_ICON, group.icon().toString());
        groupData.putInt(GROUP_COLOR, group.color());
        groupData.putBoolean(GROUP_INBUILT, group.inbuilt());
        groupData.putBoolean(GROUP_HIDDEN, group.hidden());
        groupData.putInt(GROUP_SORT_INDEX, group.sortIndex());
        groupRegistry.put(group.identifier().toString(), groupData);
    }

    @Override
    public List<UUID> getSortingIndex(Player player) {
        final var sortingIndex = getSortingIndexData(getWaystonesData(player));
        final var result = new ArrayList<UUID>();
        for (final var entry : sortingIndex) {
            entry.asString().map(UUID::fromString).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public void setSortingIndex(Player player, List<UUID> sortingIndex) {
        final var sortingIndexData = getSortingIndexData(getWaystonesData(player));
        sortingIndexData.clear();
        for (final var waystoneUid : sortingIndex) {
            sortingIndexData.add(StringTag.valueOf(waystoneUid.toString()));
        }
    }

    @Override
    public List<UUID> ensureSortingIndex(Player player, Collection<? extends Waystone> waystones) {
        final var sortingIndexData = getSortingIndexData(getWaystonesData(player));
        final var sortingIndex = new ArrayList<UUID>();
        final var existing = new HashSet<UUID>();
        for (final var sortingIndexEntry : sortingIndexData) {
            sortingIndexEntry.asString().map(UUID::fromString).ifPresent(waystoneUid -> {
                if (existing.add(waystoneUid)) {
                    sortingIndex.add(waystoneUid);
                }
            });
        }

        for (final var waystone : waystones) {
            final var waystoneUid = waystone.getWaystoneUid();
            if (!existing.contains(waystoneUid)) {
                sortingIndex.add(waystoneUid);
                sortingIndexData.add(StringTag.valueOf(waystoneUid.toString()));
            }
        }

        return sortingIndex;
    }

    @Override
    public void sortWaystoneAsFirst(Player player, UUID waystoneUid) {
        final var sortingIndex = getSortingIndexData(getWaystonesData(player));
        for (int i = 0; i < sortingIndex.size(); i++) {
            final var sortingIndexEntry = sortingIndex.get(i);
            if (sortingIndexEntry.asString().map(it -> waystoneUid.toString().equals(it)).orElse(false)) {
                sortingIndex.remove(i);
                sortingIndex.add(0, sortingIndexEntry);
                break;
            }
        }
    }

    @Override
    public void sortWaystoneAsLast(Player player, UUID waystoneUid) {
        final var sortingIndex = getSortingIndexData(getWaystonesData(player));
        for (int i = 0; i < sortingIndex.size(); i++) {
            final var sortingIndexEntry = sortingIndex.get(i);
            if (sortingIndexEntry.asString().map(it -> waystoneUid.toString().equals(it)).orElse(false)) {
                sortingIndex.remove(i);
                sortingIndex.add(sortingIndexEntry);
                break;
            }
        }
    }

    @Override
    public void sortWaystoneSwap(Player player, UUID waystoneUid, UUID otherWaystoneUid) {
        final var sortingIndex = getSortingIndexData(getWaystonesData(player));
        int waystoneIndex = -1;
        int otherWaystoneIndex = -1;
        for (int i = 0; i < sortingIndex.size(); i++) {
            final var sortingIndexEntry = sortingIndex.get(i);
            if (sortingIndexEntry.asString().map(it -> waystoneUid.toString().equals(it)).orElse(false)) {
                waystoneIndex = i;
            } else if (sortingIndexEntry.asString().map(it -> otherWaystoneUid.toString().equals(it)).orElse(false)) {
                otherWaystoneIndex = i;
            }
        }

        if (waystoneIndex != -1 && otherWaystoneIndex != -1) {
            Collections.swap(sortingIndex, waystoneIndex, otherWaystoneIndex);
        }
    }

    @Override
    public void deactivateWaystone(Player player, Waystone waystone) {
        final var data = getWaystonesData(player);
        ListTag activatedWaystones = getActivatedWaystonesData(data);
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (int i = activatedWaystones.size() - 1; i >= 0; i--) {
            Tag activatedWaystone = activatedWaystones.get(i);
            if (activatedWaystone.asString().map(waystoneUid::equals).orElse(false)) {
                activatedWaystones.remove(i);
                break;
            }
        }
    }

    private static ListTag getActivatedWaystonesData(CompoundTag data) {
        ListTag list = data.getListOrEmpty(ACTIVATED_WAYSTONES);
        data.put(ACTIVATED_WAYSTONES, list);
        return list;
    }

    private static ListTag getSortingIndexData(CompoundTag data) {
        ListTag list = data.getList(SORTING_INDEX).orElseGet(() -> createSortingIndexFromLegacy(data));
        data.put(SORTING_INDEX, list);
        return list;
    }

    private static CompoundTag getAliasesData(CompoundTag data) {
        CompoundTag aliases = data.getCompoundOrEmpty(ALIASES);
        data.put(ALIASES, aliases);
        return aliases;
    }

    private static Optional<Component> readComponent(@Nullable Tag componentTag) {
        if (componentTag == null) {
            return Optional.empty();
        }
        return ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, componentTag).result();
    }

    private static void writeComponent(CompoundTag data, String key, Component component) {
        ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, component)
                .result()
                .ifPresent(componentTag -> data.put(key, componentTag));
    }

    private static CompoundTag getGroupsData(CompoundTag data) {
        CompoundTag groups = data.getCompoundOrEmpty(GROUPS);
        data.put(GROUPS, groups);
        return groups;
    }

    private static CompoundTag getGroupRegistryData(CompoundTag data) {
        CompoundTag groupRegistry = data.getCompoundOrEmpty(GROUP_REGISTRY);
        data.put(GROUP_REGISTRY, groupRegistry);
        return groupRegistry;
    }

    private static CompoundTag getWaystonesData(Player player) {
        CompoundTag persistedData = Balm.hooks().getPersistentData(player);
        CompoundTag compound = persistedData.getCompoundOrEmpty(TAG_NAME);
        persistedData.put(TAG_NAME, compound);
        return compound;
    }

    private static ListTag createSortingIndexFromLegacy(CompoundTag data) {
        final var activatedWaystones = getActivatedWaystonesData(data);
        if (activatedWaystones.isEmpty()) {
            return new ListTag();
        }

        final var sortingIndex = new ListTag();
        sortingIndex.addAll(activatedWaystones);
        return sortingIndex;
    }

}
