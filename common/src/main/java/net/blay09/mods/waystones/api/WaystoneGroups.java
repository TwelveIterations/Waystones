package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public final class WaystoneGroups {
    public static final WaystoneGroup GLOBAL = new WaystoneGroupImpl(
            id("global"),
            id("global"),
            0xFFF5C542,
            true);

    public static final Identifier DIMENSION_ICON = id("dimension");

    private WaystoneGroups() {
    }

    public static WaystoneGroup dimension(ResourceKey<Level> dimension) {
        final var location = dimension.identifier();
        final var identifier = id("dimension/" + location.getNamespace() + "/" + location.getPath());
        return new WaystoneGroupImpl(identifier, DIMENSION_ICON, 0xFF6DB4FF, true);
    }

    public static Set<Identifier> getDynamicGroups(Waystone waystone) {
        final var groups = new LinkedHashSet<Identifier>();
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
