
package net.blay09.mods.waystones.core;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PersistentPlayerWaystoneData implements IPlayerWaystoneData {
    private static final String TAG_NAME = "WaystonesData";
    private static final String ACTIVATED_WAYSTONES = "Waystones";
    private static final String SORTING_INDEX = "SortingIndex";
    private static final String COOLDOWNS = "Cooldowns";
    private static final String ALIASES = "Aliases";
    private static final String GROUPS = "Groups";
    private static final String GROUP_REGISTRY = "GroupRegistry";
    private static final String GROUP_ID = "Id";
    private static final String GROUP_ICON = "Icon";
    private static final String GROUP_COLOR = "Color";
    private static final String GROUP_INBUILT = "Inbuilt";

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
            if (waystoneUid.equals(activatedWaystone.getAsString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Waystone> getWaystones(Player player) {
        final var activatedWaystonesTag = getActivatedWaystonesData(getWaystonesData(player));
        final var waystones = new ArrayList<Waystone>();
        for (final var iterator = activatedWaystonesTag.iterator(); iterator.hasNext(); ) {
            final var activatedWaystoneTag = iterator.next();
            final var proxy = new WaystoneProxy(player.getServer(), UUID.fromString(activatedWaystoneTag.getAsString()));
            if (proxy.isValid()) {
                waystones.add(proxy);
            } else {
                iterator.remove();
            }
        }

        return waystones;
    }

    @Override
    public Optional<Component> getWaystoneAlias(Player player, UUID waystoneUid) {
        final var aliases = getAliasesData(getWaystonesData(player));
        final var aliasKey = waystoneUid.toString();
        return aliases.contains(aliasKey) ? readAlias(aliases.get(aliasKey)) : Optional.empty();
    }

    @Override
    public void setWaystoneAlias(Player player, UUID waystoneUid, @Nullable Component alias) {
        final var aliases = getAliasesData(getWaystonesData(player));
        if (alias != null) {
            writeAlias(aliases, waystoneUid, alias);
        } else {
            aliases.remove(waystoneUid.toString());
        }
    }

    @Override
    public Collection<WaystoneGroup> getWaystoneGroupRegistry(Player player) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        final var groups = new ArrayList<WaystoneGroup>();
        final var existing = new HashSet<ResourceLocation>();
        for (final var groupId : groupRegistry.getAllKeys()) {
            final var group = readGroup(groupRegistry.getCompound(groupId));
            if (group != null && existing.add(group.identifier())) {
                groups.add(group);
            }
        }
        return List.copyOf(groups);
    }

    @Override
    public void setWaystoneGroupRegistry(Player player, Collection<WaystoneGroup> groups) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        for (final var groupId : List.copyOf(groupRegistry.getAllKeys())) {
            groupRegistry.remove(groupId);
        }
        final var normalizedGroups = new LinkedHashMap<ResourceLocation, WaystoneGroup>();
        for (final var group : groups) {
            normalizedGroups.putIfAbsent(group.identifier(), group);
        }
        for (final var group : normalizedGroups.values()) {
            writeGroup(groupRegistry, group);
        }
    }

    @Override
    public void addWaystoneGroups(Player player, Collection<WaystoneGroup> groups) {
        final var groupRegistry = getGroupRegistryData(getWaystonesData(player));
        for (final var group : groups) {
            final var existingGroup = readGroup(groupRegistry.getCompound(group.identifier().toString()));
            if (existingGroup == null || group.inbuilt() && !existingGroup.inbuilt()) {
                writeGroup(groupRegistry, group);
            }
        }
    }

    @Override
    public Set<ResourceLocation> getConfiguredWaystoneGroups(Player player, UUID waystoneUid) {
        final var groupsByWaystone = getGroupsData(getWaystonesData(player));
        final var groupsData = groupsByWaystone.getList(waystoneUid.toString(), Tag.TAG_STRING);
        final var configuredGroups = new HashSet<ResourceLocation>();
        for (final var groupEntry : groupsData) {
            final var groupId = ResourceLocation.tryParse(groupEntry.getAsString());
            if (groupId != null) {
                configuredGroups.add(groupId);
            }
        }
        return configuredGroups;
    }

    private static @Nullable WaystoneGroup readGroup(Tag groupEntry) {
        if (groupEntry instanceof CompoundTag groupData) {
            final var id = ResourceLocation.tryParse(groupData.getString(GROUP_ID));
            if (id == null) {
                return null;
            }

            final var icon = groupData.contains(GROUP_ICON) ? ResourceLocation.tryParse(groupData.getString(GROUP_ICON)) : id;
            final var color = groupData.contains(GROUP_COLOR) ? groupData.getInt(GROUP_COLOR) : 0xFFFFFFFF;
            final var inbuilt = groupData.contains(GROUP_INBUILT) && groupData.getBoolean(GROUP_INBUILT);
            return new WaystoneGroupImpl(id, icon != null ? icon : id, color, inbuilt);
        }

        return null;
    }

    @Override
    public void setConfiguredWaystoneGroups(Player player, UUID waystoneUid, Set<ResourceLocation> groupIds) {
        final var groupsByWaystone = getGroupsData(getWaystonesData(player));
        writeGroupIds(groupsByWaystone, waystoneUid, groupIds);
    }

    private static void writeGroupIds(CompoundTag groupsByWaystone, UUID waystoneUid, Collection<ResourceLocation> groupIds) {
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
        groupData.putString(GROUP_ICON, group.icon().toString());
        groupData.putInt(GROUP_COLOR, group.color());
        groupData.putBoolean(GROUP_INBUILT, group.inbuilt());
        groupRegistry.put(group.identifier().toString(), groupData);
    }

    @Override
    public List<UUID> getSortingIndex(Player player) {
        final var sortingIndex = getSortingIndexData(getWaystonesData(player));
        return sortingIndex.stream().map(entry -> UUID.fromString(entry.getAsString())).toList();
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
            final var waystoneUid = UUID.fromString(sortingIndexEntry.getAsString());
            if (existing.add(waystoneUid)) {
                sortingIndex.add(waystoneUid);
            }
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
            if (waystoneUid.toString().equals(sortingIndexEntry.getAsString())) {
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
            if (waystoneUid.toString().equals(sortingIndexEntry.getAsString())) {
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
            if (waystoneUid.toString().equals(sortingIndexEntry.getAsString())) {
                waystoneIndex = i;
            } else if (otherWaystoneUid.toString().equals(sortingIndexEntry.getAsString())) {
                otherWaystoneIndex = i;
            }
        }

        if (waystoneIndex != -1 && otherWaystoneIndex != -1) {
            Collections.swap(sortingIndex, waystoneIndex, otherWaystoneIndex);
        }
    }

    @Override
    public void deactivateWaystone(Player player, Waystone waystone) {
        CompoundTag data = getWaystonesData(player);
        ListTag activatedWaystones = getActivatedWaystonesData(data);
        String waystoneUid = waystone.getWaystoneUid().toString();
        for (int i = activatedWaystones.size() - 1; i >= 0; i--) {
            Tag activatedWaystone = activatedWaystones.get(i);
            if (waystoneUid.equals(activatedWaystone.getAsString())) {
                activatedWaystones.remove(i);
                break;
            }
        }
    }

    @Override
    public Map<ResourceLocation, Long> getCooldowns(Player player) {
        final var waystonesData = getWaystonesData(player);
        final var cooldowns = waystonesData.getCompound(COOLDOWNS);
        final var cooldownMap = new HashMap<ResourceLocation, Long>();
        for (final var key : cooldowns.getAllKeys()) {
            cooldownMap.put(ResourceLocation.parse(key), cooldowns.getLong(key));
        }

        return cooldownMap;
    }

    @Override
    public void resetCooldowns(Player player) {
        final var waystonesData = getWaystonesData(player);
        waystonesData.put(COOLDOWNS, new CompoundTag());
    }

    @Override
    public long getCooldownUntil(Player player, ResourceLocation key) {
        final var waystonesData = getWaystonesData(player);
        final var cooldowns = waystonesData.getCompound(COOLDOWNS);
        return cooldowns.getLong(key.toString());
    }

    @Override
    public void setCooldownUntil(Player player, ResourceLocation key, long timeStamp) {
        final var waystonesData = getWaystonesData(player);
        final var cooldowns = waystonesData.getCompound(COOLDOWNS);
        cooldowns.putLong(key.toString(), timeStamp);
        waystonesData.put(COOLDOWNS, cooldowns);
    }

    private static ListTag getActivatedWaystonesData(CompoundTag data) {
        ListTag list = data.getList(ACTIVATED_WAYSTONES, Tag.TAG_STRING);
        data.put(ACTIVATED_WAYSTONES, list);
        return list;
    }

    private static ListTag getSortingIndexData(CompoundTag data) {
        ListTag list = data.contains(SORTING_INDEX) ? data.getList(SORTING_INDEX, Tag.TAG_STRING) : createSortingIndexFromLegacy(data);
        data.put(SORTING_INDEX, list);
        return list;
    }

    private static CompoundTag getAliasesData(CompoundTag data) {
        CompoundTag aliases = data.contains(ALIASES, Tag.TAG_COMPOUND) ? data.getCompound(ALIASES) : new CompoundTag();
        data.put(ALIASES, aliases);
        return aliases;
    }

    private static Optional<Component> readAlias(@Nullable Tag aliasTag) {
        if (aliasTag == null) {
            return Optional.empty();
        }
        return ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, aliasTag).result();
    }

    private static void writeAlias(CompoundTag aliases, UUID waystoneUid, Component alias) {
        ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, alias)
                .result()
                .ifPresent(aliasTag -> aliases.put(waystoneUid.toString(), aliasTag));
    }

    private static CompoundTag getGroupsData(CompoundTag data) {
        CompoundTag groups = data.contains(GROUPS, Tag.TAG_COMPOUND) ? data.getCompound(GROUPS) : new CompoundTag();
        data.put(GROUPS, groups);
        return groups;
    }

    private static CompoundTag getGroupRegistryData(CompoundTag data) {
        CompoundTag groupRegistry = data.contains(GROUP_REGISTRY, Tag.TAG_COMPOUND) ? data.getCompound(GROUP_REGISTRY) : new CompoundTag();
        data.put(GROUP_REGISTRY, groupRegistry);
        return groupRegistry;
    }

    private static CompoundTag getWaystonesData(Player player) {
        CompoundTag persistedData = Balm.getHooks().getPersistentData(player);
        CompoundTag compound = persistedData.getCompound(TAG_NAME);
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

    @Override
    public Optional<Waystone> findWaystoneByName(Player player, String name) {
        return WaystoneManagerImpl.get(player.getServer()).findWaystoneByName(name);
    }
}
