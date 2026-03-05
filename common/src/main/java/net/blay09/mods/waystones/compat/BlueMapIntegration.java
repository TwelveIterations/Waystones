package net.blay09.mods.waystones.compat;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesLoadedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

        WaystonesLoadedEvent.EVENT.register(this::onWaystonesLoaded);
        WaystoneInitializedEvent.EVENT.register(this::onWaystoneInitialized);
        WaystoneUpdatedEvent.EVENT.register(this::onWaystoneUpdated);
        WaystoneRemovedEvent.EVENT.register(this::onWaystoneRemoved);
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

    private static boolean isSupportedWaystone(Waystone waystone) {
        return isSupportedWaystoneType(waystone.getWaystoneType()) && !waystone.isTransient();
    }

    private static boolean isSupportedWaystoneType(Identifier waystoneType) {
        final var config = WaystonesConfig.getActive().blueMap;
        if (waystoneType.equals(WaystoneTypes.WAYSTONE)) {
            return config.includeWaystones || config.includeUndiscoveredWaystones;
        } else if (WaystoneTypes.isSharestone(waystoneType)) {
            return config.includeSharestones;
        }
        return false;
    }

    public boolean isEnabled() {
        return WaystonesConfig.getActive().blueMap.enabled;
    }

    private void onWaystoneInitialized(WaystoneInitializedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.waystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.addWaystoneMarker(event.waystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystoneUpdated(WaystoneUpdatedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.waystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.addWaystoneMarker(event.waystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystoneRemoved(WaystoneRemovedEvent event) {
        if (!isEnabled()) {
            return;
        }

        ResourceKey<Level> dimensionId = event.waystone().getDimension();
        final var levelMarkers = levelMarkersByDimension.computeIfAbsent(dimensionId, LevelMarkers::new);
        levelMarkers.removeWaystoneMarker(event.waystone());
        if (api != null) {
            levelMarkers.update(api);
        }
    }

    private void onWaystonesLoaded(WaystonesLoadedEvent event) {
        if (!isEnabled()) {
            return;
        }

        final var waystonesByDimension = event.waystoneManager().getWaystones().stream()
                .filter(BlueMapIntegration::isSupportedWaystone)
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

        private final MarkerSet undiscoveredWaystoneMarkers = MarkerSet.builder()
                .label("Waystones (undiscovered)")
                .build();

        private final MarkerSet sharestoneMarkers = MarkerSet.builder()
                .label("Sharestones")
                .build();

        private final ResourceKey<Level> level;

        private LevelMarkers(ResourceKey<Level> level) {
            this.level = level;
        }

        public void update(BlueMapAPI api) {
            final var config = WaystonesConfig.getActive().blueMap;
            api.getWorld(level).ifPresent(world -> {
                for (var map : world.getMaps()) {
                    if (config.includeWaystones) {
                        map.getMarkerSets().put("waystones:waystones", waystoneMarkers);
                    } else {
                        map.getMarkerSets().remove("waystones:waystones");
                    }
                    if (config.includeUndiscoveredWaystones) {
                        map.getMarkerSets().put("waystones:undiscovered_waystones", undiscoveredWaystoneMarkers);
                    } else {
                        map.getMarkerSets().remove("waystones:undiscovered_waystones");
                    }
                    if (config.includeSharestones) {
                        map.getMarkerSets().put("waystones:sharestones", sharestoneMarkers);
                    } else {
                        map.getMarkerSets().remove("waystones:sharestones");
                    }
                }
            });
        }

        public void createFromWaystones(List<Waystone> waystones) {
            waystoneMarkers.getMarkers().clear();
            undiscoveredWaystoneMarkers.getMarkers().clear();
            sharestoneMarkers.getMarkers().clear();

            for (final var waystone : waystones) {
                addWaystoneMarker(waystone);
            }
        }

        public void addWaystoneMarker(Waystone waystone) {
            final var marker = createWaystoneMarker(waystone);
            final var markerId = getMarkerId(waystone);
            final var config = WaystonesConfig.getActive().blueMap;
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                if (config.includeSharestones) {
                    sharestoneMarkers.put(markerId, marker);
                }
            } else {
                if (waystone.hasName()) {
                    if (config.includeWaystones) {
                        waystoneMarkers.put(markerId, marker);
                    }
                } else {
                    if (config.includeUndiscoveredWaystones) {
                        undiscoveredWaystoneMarkers.put(markerId, marker);
                    }
                }
            }
        }

        public void removeWaystoneMarker(Waystone waystone) {
            final var markerId = getMarkerId(waystone);
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                sharestoneMarkers.remove(markerId);
            } else {
                waystoneMarkers.remove(markerId);
                undiscoveredWaystoneMarkers.remove(markerId);
            }
        }
    }
}
