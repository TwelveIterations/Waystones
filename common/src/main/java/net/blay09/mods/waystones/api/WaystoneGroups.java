package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

import static net.blay09.mods.waystones.Waystones.id;

public final class WaystoneGroups {
    public static final ResourceLocation COMMUNITY_HUBS_ICON = id("groups/community_hubs");
    public static final ResourceLocation DUNGEONS_ICON = id("groups/dungeons");
    public static final ResourceLocation FAVORITES_ICON = id("groups/favorites");
    public static final ResourceLocation PLAYER_HOMES_ICON = id("groups/player_homes");
    public static final ResourceLocation RESOURCE_SITES_ICON = id("groups/resource_sites");
    public static final ResourceLocation OVERWORLD_ICON = id("groups/dimension/minecraft/overworld");
    public static final ResourceLocation THE_NETHER_ICON = id("groups/dimension/minecraft/the_nether");
    public static final ResourceLocation THE_END_ICON = id("groups/dimension/minecraft/the_end");
    public static final ResourceLocation DIMENSION_ICON = id("groups/dimension");
    public static final ResourceLocation VILLAGES_ICON = id("groups/villages");
    public static final ResourceLocation GLOBAL_ICON = id("groups/global");

    // TODO Would be nice to expose an API for this and getDimensionIcon
    public static final List<ResourceLocation> PRESET_ICONS = List.of(
            COMMUNITY_HUBS_ICON,
            DUNGEONS_ICON,
            FAVORITES_ICON,
            PLAYER_HOMES_ICON,
            RESOURCE_SITES_ICON,
            OVERWORLD_ICON,
            THE_NETHER_ICON,
            THE_END_ICON,
            DIMENSION_ICON,
            VILLAGES_ICON,
            GLOBAL_ICON);

    public static final WaystoneGroup FAVORITES = new WaystoneGroupImpl(
            id("favorites"),
            Component.translatable("waystones.groups.favorites"),
            FAVORITES_ICON,
            0xFFF5C542,
            true,
            false,
            -2);

    public static final WaystoneGroup GLOBAL = new WaystoneGroupImpl(
            id("global"),
            Component.translatable("waystones.groups.global"),
            GLOBAL_ICON,
            0xFFF5C542,
            true,
            false,
            -1);

    private WaystoneGroups() {
    }

    public static List<WaystoneGroup> sorted(Collection<WaystoneGroup> groups) {
        return groups.stream()
                .sorted(Comparator.comparingInt(WaystoneGroup::sortIndex)
                        .thenComparing(group -> group.name().getString(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(group -> group.identifier().toString()))
                .toList();
    }

    public static List<WaystoneGroup> normalizeSortIndices(Collection<WaystoneGroup> groups) {
        final var result = new ArrayList<WaystoneGroup>();
        final var existing = new HashSet<ResourceLocation>();
        for (final var group : groups) {
            if (existing.add(group.identifier())) {
                result.add(withSortIndex(group, result.size()));
            }
        }
        return result;
    }

    public static WaystoneGroup withSortIndex(WaystoneGroup group, int sortIndex) {
        return new WaystoneGroupImpl(group.identifier(), group.name(), group.icon(), group.color(), group.inbuilt(), group.hidden(), sortIndex);
    }

    public static Optional<WaystoneGroup> findGroup(Collection<WaystoneGroup> groups, ResourceLocation groupId) {
        return groups.stream()
                .filter(group -> group.identifier().equals(groupId))
                .findFirst();
    }

    public static int indexOfGroup(List<WaystoneGroup> groups, WaystoneGroup group) {
        return indexOfGroup(groups, group.identifier());
    }

    public static int indexOfGroup(List<WaystoneGroup> groups, ResourceLocation groupId) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).identifier().equals(groupId)) {
                return i;
            }
        }
        return -1;
    }

    public static Optional<WaystoneGroup> removeGroup(List<WaystoneGroup> groups, ResourceLocation groupId) {
        final int index = indexOfGroup(groups, groupId);
        return index != -1 ? Optional.of(groups.remove(index)) : Optional.empty();
    }

    public static Optional<WaystoneGroup> getFirstGroup(Player player, Waystone waystone) {
        final var groupRegistry = PlayerWaystoneManager.getWaystoneGroupRegistry(player);
        return getFirstGroup(groupRegistry, waystone);
    }

    public static Optional<WaystoneGroup> getFirstGroup(Collection<WaystoneGroup> groups, Waystone waystone) {
        final var waystoneGroups = waystone.getWaystoneGroups();
        return groups.stream()
                .filter(group -> waystoneGroups.contains(group.identifier()))
                .findFirst();
    }

    public static WaystoneGroup dimension(ResourceKey<Level> dimension) {
        final var location = dimension.location();
        final var identifier = id("dimension/" + location.getNamespace() + "/" + location.getPath());
        final var name = Component.translatable("waystones.groups." + identifier.getPath().replace('/', '.'));
        return new WaystoneGroupImpl(identifier, name, getDimensionIcon(dimension), 0xFF6DB4FF, true, false, 0);
    }

    private static ResourceLocation getDimensionIcon(ResourceKey<Level> dimension) {
        if (dimension == Level.OVERWORLD) {
            return OVERWORLD_ICON;
        } else if (dimension == Level.NETHER) {
            return THE_NETHER_ICON;
        } else if (dimension == Level.END) {
            return THE_END_ICON;
        }

        return DIMENSION_ICON;
    }

    public static Set<ResourceLocation> getDynamicGroups(Waystone waystone) {
        final var groups = new LinkedHashSet<ResourceLocation>();
        for (final var group : getDynamicGroupDefinitions(waystone)) {
            groups.add(group.identifier());
        }
        return Collections.unmodifiableSet(groups);
    }

    public static List<WaystoneGroup> getDynamicGroupDefinitions(Waystone waystone) {
        final var groups = new ArrayList<WaystoneGroup>();
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
            groups.add(GLOBAL);
        }
        groups.add(dimension(waystone.getDimension()));
        return List.copyOf(groups);
    }
}
