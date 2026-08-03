package net.blay09.mods.waystones.compat;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.event.WaystoneInitializedEvent;
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdatedEvent;
import net.blay09.mods.waystones.api.event.WaystonesLoadedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.resources.ResourceLocation;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DynmapIntegration extends DynmapCommonAPIListener {

    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    private DynmapCommonAPI api;
    private @Nullable String overworldLevelName;
    private @Nullable MarkerSet waystoneMarkers;
    private @Nullable MarkerSet sharestoneMarkers;

    public DynmapIntegration() {
        DynmapCommonAPIListener.register(this);

        Balm.getEvents().onEvent(ServerStartedEvent.class, this::onServerStarted);
        Balm.getEvents().onEvent(ServerStoppedEvent.class, this::onServerStopped);
        Balm.getEvents().onEvent(WaystonesLoadedEvent.class, this::onWaystonesLoaded);
        Balm.getEvents().onEvent(WaystoneInitializedEvent.class, this::onWaystoneInitialized);
        Balm.getEvents().onEvent(WaystoneUpdatedEvent.class, this::onWaystoneUpdated);
        Balm.getEvents().onEvent(WaystoneRemovedEvent.class, this::onWaystoneRemoved);
    }

    public static String getMarkerId(Waystone waystone) {
        return waystone.getWaystoneUid().toString();
    }

    private Marker createWaystoneMarker(MarkerSet markerSet, Waystone waystone) {
        return markerSet.createMarker(getMarkerId(waystone),
                waystone.getEffectiveName().getString(),
                false,
                getDynmapWorldName(waystone.getDimension().location()),
                waystone.getPos().getX(),
                waystone.getPos().getY(),
                waystone.getPos().getZ(),
                markerSet.getDefaultMarkerIcon(),
                false);
    }

    private String getDynmapWorldName(ResourceLocation id) {
        return switch (id.toString()) {
            case "minecraft:overworld" -> overworldLevelName;
            case "minecraft:the_nether" -> "DIM-1";
            case "minecraft:the_end" -> "DIM1";
            default -> id.getNamespace() + "_" + id.getPath();
        };
    }

    private static boolean isSupportedWaystone(Waystone waystone) {
        return isSupportedWaystoneType(waystone.getWaystoneType()) && !waystone.isTransient();
    }

    private static boolean isSupportedWaystoneType(ResourceLocation waystoneType) {
        return waystoneType.equals(WaystoneTypes.WAYSTONE) || WaystoneTypes.isSharestone(waystoneType);
    }

    public boolean isEnabled() {
        return WaystonesConfig.getActive().compatibility.dynmap;
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
        final var markerSet = WaystoneTypes.isSharestone(waystone.getWaystoneType()) ? sharestoneMarkers : waystoneMarkers;
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
        runScheduledJobsIfReady();
    }

    @Override
    public void apiDisabled(DynmapCommonAPI api) {
        this.api = null;
    }

    private void onServerStarted(ServerStartedEvent event) {
        overworldLevelName = event.getServer().getWorldData().getLevelName();
        runScheduledJobsIfReady();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        overworldLevelName = null;
        scheduledJobsWhenReady.clear();
    }

    private void onWaystoneInitialized(WaystoneInitializedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> addWaystoneMarker(event.getWaystone()));
    }

    private void onWaystoneUpdated(WaystoneUpdatedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> addWaystoneMarker(event.getWaystone()));
    }

    private void onWaystoneRemoved(WaystoneRemovedEvent event) {
        if (!isEnabled()) {
            return;
        }

        runWhenDynmapIsReady(() -> removeWaystoneMarker(event.getWaystone()));
    }

    private void onWaystonesLoaded(WaystonesLoadedEvent event) {
        if (!isEnabled()) {
            return;
        }

        final var waystones = event.getWaystoneManager().getWaystones()
                .filter(DynmapIntegration::isSupportedWaystone)
                .toList();
        runWhenDynmapIsReady(() -> createFromWaystones(waystones));
    }

    private void runWhenDynmapIsReady(Runnable runnable) {
        if (api != null && overworldLevelName != null) {
            runnable.run();
        } else {
            scheduledJobsWhenReady.add(runnable);
        }
    }

    private void runScheduledJobsIfReady() {
        if (api == null || overworldLevelName == null) {
            return;
        }

        for (Runnable scheduledJob : scheduledJobsWhenReady) {
            scheduledJob.run();
        }
        scheduledJobsWhenReady.clear();
    }
}
