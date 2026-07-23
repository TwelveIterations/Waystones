package net.blay09.mods.waystones.compat;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesLoadedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.resources.Identifier;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DynmapIntegration extends DynmapCommonAPIListener {

    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    private @Nullable DynmapCommonAPI api;
    private MarkerSet waystoneMarkers;
    private MarkerSet sharestoneMarkers;

    public DynmapIntegration() {
        DynmapCommonAPIListener.register(this);

        WaystonesLoadedEvent.EVENT.register(this::onWaystonesLoaded);
        WaystoneInitializedEvent.EVENT.register(this::onWaystoneInitialized);
        WaystoneUpdatedEvent.EVENT.register(this::onWaystoneUpdated);
        WaystoneRemovedEvent.EVENT.register(this::onWaystoneRemoved);
    }

    public static String getMarkerId(Waystone waystone) {
        return waystone.getWaystoneUid().toString();
    }

    public static Marker createWaystoneMarker(MarkerSet markerSet, Waystone waystone) {
        return markerSet.createMarker(getMarkerId(waystone),
                waystone.getEffectiveName().getString(),
                false,
                getDynmapWorldName(waystone.getDimension().identifier()),
                waystone.getPos().getX(),
                waystone.getPos().getY(),
                waystone.getPos().getZ(),
                markerSet.getDefaultMarkerIcon(),
                false);
    }

    private static String getDynmapWorldName(Identifier id) {
        return switch (id.toString()) {
            case "minecraft:overworld" -> Objects.requireNonNull(Balm.platform().server()).getWorldData().getLevelName();
            case "minecraft:the_nether" -> "DIM-1";
            case "minecraft:the_end" -> "DIM1";
            default -> id.getNamespace() + "_" + id.getPath();
        };
    }

    private static boolean isSupportedWaystone(Waystone waystone) {
        return isSupportedWaystoneType(waystone.getWaystoneKind()) && !waystone.isTransient();
    }

    private static boolean isSupportedWaystoneType(Identifier waystoneType) {
        return waystoneType.equals(WaystoneKinds.WAYSTONE) || WaystoneKinds.isSharestone(waystoneType);
    }

    public boolean isEnabled() {
        return WaystonesConfig.getActive().dynmap.enabled;
    }

    private void prepareMarkerSets() {
        if (waystoneMarkers == null) {
            waystoneMarkers = api.getMarkerAPI().createMarkerSet("waystones:waystones", "Waystones", null, false);
        }
        if (sharestoneMarkers == null) {
            sharestoneMarkers = api.getMarkerAPI().createMarkerSet("waystones:sharestones", "Sharestones", null, false);
        }
    }

    public void createFromWaystones(List<Waystone> waystones) {
        if (waystoneMarkers != null) {
            waystoneMarkers.deleteMarkerSet();
        }
        if (sharestoneMarkers != null) {
            sharestoneMarkers.deleteMarkerSet();
        }
        prepareMarkerSets();

        for (final var waystone : waystones) {
            addWaystoneMarker(waystone);
        }
    }

    public void addWaystoneMarker(Waystone waystone) {
        prepareMarkerSets();
        final var markerSet = WaystoneKinds.isSharestone(waystone.getWaystoneKind()) ? sharestoneMarkers : waystoneMarkers;
        createWaystoneMarker(markerSet, waystone);
    }

    public void removeWaystoneMarker(Waystone waystone) {
        if (waystoneMarkers != null) {
            return;
        }

        final var markerId = getMarkerId(waystone);
        final var marker = waystoneMarkers.findMarker(markerId);
        if (marker != null) {
            marker.deleteMarker();
        }
    }

    @Override
    public void apiEnabled(DynmapCommonAPI api) {
        this.api = api;

        for (Runnable scheduledJob : scheduledJobsWhenReady) {
            scheduledJob.run();
        }
        scheduledJobsWhenReady.clear();
    }

    @Override
    public void apiDisabled(DynmapCommonAPI api) {
        this.api = null;
    }

    private void onWaystoneInitialized(WaystoneInitializedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> addWaystoneMarker(event.waystone()));
    }

    private void onWaystoneUpdated(WaystoneUpdatedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> addWaystoneMarker(event.waystone()));
    }

    private void onWaystoneRemoved(WaystoneRemovedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> removeWaystoneMarker(event.waystone()));
    }

    private void onWaystonesLoaded(WaystonesLoadedEvent event) {
        if (!isEnabled()) {
            return;
        }

        final var waystones = event.waystoneManager().getWaystones().stream()
                .filter(DynmapIntegration::isSupportedWaystone)
                .toList();
        runWhenDynmapIsReady(() -> createFromWaystones(waystones));
    }

    private void runWhenDynmapIsReady(Runnable runnable) {
        if (api != null) {
            runnable.run();
        } else {
            scheduledJobsWhenReady.add(runnable);
        }
    }
}
