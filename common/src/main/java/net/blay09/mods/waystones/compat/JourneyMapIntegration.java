package net.blay09.mods.waystones.compat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.event.ClientEventRegistry;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.event.WaystoneRemoveReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyMapIntegration implements IClientPlugin {

    private static final Gson gson = new Gson();

    private record WaystonesWaypointData(UUID waystoneId, ResourceLocation waystoneType) {

        private static final MapCodec<WaystonesWaypointData> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                UUIDUtil.CODEC.fieldOf("waystoneId").forGetter(WaystonesWaypointData::waystoneId),
                ResourceLocation.CODEC.fieldOf("type").forGetter(WaystonesWaypointData::waystoneType)).apply(builder, WaystonesWaypointData::new));

        public static Optional<WaystonesWaypointData> decode(String customData) {
            final var jsonElement = gson.fromJson(customData, JsonElement.class);
            return JsonOps.INSTANCE.getMap(jsonElement).flatMap((it) -> CODEC.decode(JsonOps.INSTANCE, it)).resultOrPartial();
        }

        public String encode() {
            final var jsonElement = CODEC.encode(this, JsonOps.INSTANCE, JsonOps.INSTANCE.mapBuilder()).build(JsonOps.INSTANCE.empty()).getOrThrow();
            return gson.toJson(jsonElement);
        }
    }

    private IClientAPI api;
    private boolean journeyMapReady;
    private final Map<UUID, String> waystoneToWaypoint = new HashMap<>();

    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    private static JourneyMapIntegration instance;

    public JourneyMapIntegration() {
        instance = this;
        Balm.getEvents().onEvent(WaystonesListReceivedEvent.class, this::onWaystonesListReceived);
        Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, this::onWaystoneUpdateReceived);
        Balm.getEvents().onEvent(WaystoneRemoveReceivedEvent.class, this::onWaystoneRemoveReceived);
    }

    @Override
    public void initialize(IClientAPI iClientAPI) {
        api = iClientAPI;

        // This fires after all waypoints have been loaded
        ClientEventRegistry.MAPPING_EVENT.subscribe(Waystones.MOD_ID, this::onMappingEvent);
    }

    /**
     * This will be null if Journeymap is not loaded.
     */
    @Nullable
    public static JourneyMapIntegration getInstance() {
        return instance;
    }

    @Override
    public String getModId() {
        return Waystones.MOD_ID;
    }

    public void onMappingEvent(MappingEvent event) {
        if (event.getStage() == MappingEvent.Stage.MAPPING_STARTED) {
            final var waypoints = api.getWaypoints(Waystones.MOD_ID);
            for (final var waypoint : waypoints) {
                WaystonesWaypointData.decode(waypoint.getCustomData())
                        .ifPresent(customData -> waystoneToWaypoint.put(customData.waystoneId(), waypoint.getGuid()));
            }

            journeyMapReady = true;

            for (Runnable scheduledJob : scheduledJobsWhenReady) {
                scheduledJob.run();
            }
            scheduledJobsWhenReady.clear();
        } else if (event.getStage() == MappingEvent.Stage.MAPPING_STOPPED) {
            journeyMapReady = false;
            waystoneToWaypoint.clear();
        }
    }

    public void onWaystonesListReceived(WaystonesListReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystoneType())) {
            runWhenJourneyMapIsReady(() -> updateAllWaypoints(event.getWaystoneType(), event.getWaystones().stream().filter(this::isSupportedWaystone).toList()));
        }
    }

    private boolean isSupportedWaystone(Waystone waystone) {
        return isSupportedWaystoneType(waystone.getWaystoneType()) && !waystone.isTransient();
    }

    private boolean isSupportedWaystoneType(ResourceLocation waystoneType) {
        return waystoneType.equals(WaystoneTypes.WAYSTONE) || WaystoneTypes.isSharestone(waystoneType);
    }

    private static boolean shouldManageWaypoints() {
        WaystonesConfigData config = WaystonesConfig.getActive();
        if (config.compatibility.preferJourneyMapIntegrationMod && Balm.isModLoaded("jmi")) {
            return false;
        }

        return config.compatibility.journeyMap;
    }

    public void onWaystoneUpdateReceived(WaystoneUpdateReceivedEvent event) {
        if (shouldManageWaypoints()) {
            if (isSupportedWaystone(event.getWaystone())) {
                runWhenJourneyMapIsReady(() -> updateWaypoint(event.getWaystone()));
            } else {
                runWhenJourneyMapIsReady(() -> removeWaypoint(event.getWaystone().getWaystoneUid()));
            }
        }
    }

    public void onWaystoneRemoveReceived(WaystoneRemoveReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystoneType())) {
            runWhenJourneyMapIsReady(() -> removeWaypoint(event.getWaystoneId()));
        }
    }

    private void runWhenJourneyMapIsReady(Runnable runnable) {
        if (journeyMapReady) {
            runnable.run();
        } else {
            scheduledJobsWhenReady.add(runnable);
        }
    }

    private void updateAllWaypoints(ResourceLocation waystoneType, List<Waystone> waystones) {
        final var stillExistingIds = new HashSet<UUID>();
        for (final var waystone : waystones) {
            stillExistingIds.add(waystone.getWaystoneUid());
            updateWaypoint(waystone);
        }

        final var waypoints = api.getWaypoints(Waystones.MOD_ID);
        for (final var waypoint : waypoints) {
            WaystonesWaypointData.decode(waypoint.getCustomData()).ifPresent(customData -> {
                final var waystoneUid = customData.waystoneId();
                if (waystoneType.equals(customData.waystoneType()) && !stillExistingIds.contains(waystoneUid)) {
                    api.removeWaypoint(Waystones.MOD_ID, waypoint);
                    waystoneToWaypoint.remove(waystoneUid);
                }
            });
        }
    }

    private void removeWaypoint(UUID waystoneId) {
        final var waypointId = waystoneToWaypoint.get(waystoneId);
        if (waypointId != null) {
            final var waypoint = api.getWaypoint(Waystones.MOD_ID, waypointId);
            if (waypoint != null) {
                api.removeWaypoint(Waystones.MOD_ID, waypoint);
            }
            waystoneToWaypoint.remove(waystoneId);
        }
    }

    private void updateWaypoint(Waystone waystone) {
        try {
            final var waypointId = waystoneToWaypoint.get(waystone.getWaystoneUid());
            final var waystoneName = waystone.hasName() ? waystone.getName() : Component.translatable("waystones.map.untitled_waystone");
            final var oldWaypoint = waypointId != null ? api.getWaypoint(Waystones.MOD_ID, waypointId) : null;
            final var waypoint = oldWaypoint != null ? oldWaypoint : WaypointFactory.createClientWaypoint(Waystones.MOD_ID,
                    waystone.getPos().above(2),
                    waystoneName.getString(),
                    waystone.getDimension(),
                    true);
            if (oldWaypoint != null) {
                oldWaypoint.setName(waystoneName.getString());
                waypoint.setPos(waystone.getPos().getX(), waystone.getPos().getY() + 2, waystone.getPos().getZ());
                waypoint.setPrimaryDimension(waystone.getDimension());
            }
            waypoint.setCustomData(new WaystonesWaypointData(waystone.getWaystoneUid(), waystone.getWaystoneType()).encode());
            if (oldWaypoint == null) {
                api.addWaypoint(Waystones.MOD_ID, waypoint);

                final var group = getWaystoneGroup(waystone);
                if (group != null) {
                    group.setLocked(false);
                    group.addWaypoint(waypoint);
                    group.setLocked(true);
                }
            }
            waystoneToWaypoint.put(waystone.getWaystoneUid(), waypoint.getGuid());
        } catch (Exception e) {
            Waystones.logger.warn("Failed to update waypoint for waystone {}", waystone.getWaystoneUid(), e);
        }
    }

    private WaypointGroup getWaystoneGroup(Waystone waystone) {
        if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            return Optional.ofNullable(api.getWaypointGroupByName(Waystones.MOD_ID, "Sharestones"))
                    .orElseGet(() -> {
                        final var group = WaypointFactory.createWaypointGroup(Waystones.MOD_ID, "Sharestones");
                        group.setLocked(true);
                        api.addWaypointGroup(group);
                        return group;
                    });
        } else {
            return Optional.ofNullable(api.getWaypointGroupByName(Waystones.MOD_ID, "Waystones"))
                    .orElseGet(() -> {
                        final var group = WaypointFactory.createWaypointGroup(Waystones.MOD_ID, "Waystones");
                        group.setLocked(true);
                        api.addWaypointGroup(group);
                        return group;
                    });
        }
    }
}
