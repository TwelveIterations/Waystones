package net.blay09.mods.waystones.compat;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesLoadedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlueMapIntegration {

    private final Map<ResourceKey<Level>, LevelMarkers> levelMarkersByDimension = new HashMap<>();
    private BlueMapAPI api;

    public BlueMapIntegration() {
        BlueMapAPI.onEnable(api -> {
            this.api = api;
            if (isEnabled()) {
                for (final var levelMarkers : levelMarkersByDimension.values()) {
                    levelMarkers.update(api);
                }
            }
        });
        BlueMapAPI.onDisable(api -> this.api = null);

        Balm.getEvents().onEvent(WaystonesLoadedEvent.class, this::onWaystonesLoaded);
        Balm.getEvents().onEvent(WaystoneInitializedEvent.class, this::onWaystoneInitialized);
        Balm.getEvents().onEvent(WaystoneUpdatedEvent.class, this::onWaystoneUpdated);
        Balm.getEvents().onEvent(WaystoneRemovedEvent.class, this::onWaystoneRemoved);
    }

    public static String getMarkerId(Waystone waystone) {
        return waystone.getWaystoneUid().toString();
    }

    public static POIMarker createWaystoneMarker(Waystone waystone) {
        return POIMarker.builder()
                .label(waystone.getName().getString())
                .position((double) waystone.getPos().getX(), waystone.getPos().getY(), waystone.getPos().getZ())
                .maxDistance(1000)
                .build();
    }

    private static boolean isSupportedWaystoneType(Waystone waystone) {
        return isSupportedWaystoneType(waystone.getWaystoneType());
    }

    private static boolean isSupportedWaystoneType(ResourceLocation waystoneType) {
        return waystoneType.equals(WaystoneTypes.WAYSTONE) || WaystoneTypes.isSharestone(waystoneType);
    }

    public boolean isEnabled() {
        return WaystonesConfig.getActive().compatibility.blueMap;
    }

    private void onWaystoneInitialized(WaystoneInitializedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.getWaystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.addWaystoneMarker(event.getWaystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystoneUpdated(WaystoneUpdatedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.getWaystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.addWaystoneMarker(event.getWaystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystoneRemoved(WaystoneRemovedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.getWaystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.removeWaystoneMarker(event.getWaystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystonesLoaded(WaystonesLoadedEvent event) {
        if (!isEnabled()) {
            return;
        }

        final var waystonesByDimension = event.getWaystoneManager().getWaystones().stream()
                .filter(BlueMapIntegration::isSupportedWaystoneType)
                .collect(Collectors.groupingBy(Waystone::getDimension));
        for (final var entry : waystonesByDimension.entrySet()) {
            final var levelMarkers = levelMarkersByDimension.computeIfAbsent(entry.getKey(), LevelMarkers::new);
            levelMarkers.createFromWaystones(entry.getValue());
            if (api != null) {
                levelMarkers.update(api);
            }
        }
    }

    private static class LevelMarkers {
        private final MarkerSet waystoneMarkers = MarkerSet.builder()
                .label("Waystones")
                .build();

        private final MarkerSet sharestoneMarkers = MarkerSet.builder()
                .label("Sharestones")
                .build();

        private final ResourceKey<Level> level;

        private LevelMarkers(ResourceKey<Level> level) {
            this.level = level;
        }

        public void update(BlueMapAPI api) {
            api.getWorld(level).ifPresent(world -> {
                for (var map : world.getMaps()) {
                    map.getMarkerSets().put("waystones:waystones", waystoneMarkers);
                    map.getMarkerSets().put("waystones:sharestones", sharestoneMarkers);
                }
            });
        }

        public void createFromWaystones(List<Waystone> waystones) {
            waystoneMarkers.getMarkers().clear();
            sharestoneMarkers.getMarkers().clear();

            for (final var waystone : waystones) {
                addWaystoneMarker(waystone);
            }
        }

        public void addWaystoneMarker(Waystone waystone) {
            final var marker = createWaystoneMarker(waystone);
            final var markerId = getMarkerId(waystone);
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                sharestoneMarkers.put(markerId, marker);
            } else {
                waystoneMarkers.put(markerId, marker);
            }
        }

        public void removeWaystoneMarker(Waystone waystone) {
            final var markerId = getMarkerId(waystone);
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                sharestoneMarkers.remove(markerId);
            } else {
                waystoneMarkers.remove(markerId);
            }
        }
    }
}
