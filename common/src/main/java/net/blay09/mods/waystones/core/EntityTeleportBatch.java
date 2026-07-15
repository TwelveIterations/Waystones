package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.EntityTeleportResult;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneTeleportResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class EntityTeleportBatch {
    private final WaystoneTeleportContext context;
    private final TeleportDestination destination;
    private final WaystoneTeleportResult result;
    private final Map<Entity, EntityTeleportResult> resultsByOriginalEntity = new IdentityHashMap<>();

    public EntityTeleportBatch(WaystoneTeleportContext context, TeleportDestination destination, WaystoneTeleportResult result) {
        this.context = context;
        this.destination = destination;
        this.result = result;
    }

    public void teleportEntityAndAttached(Entity entity) {
        final var mount = entity.getVehicle();
        final List<Entity> mountPassengers = new ArrayList<>();
        if (mount != null && mount == entity.getControlledVehicle()) {
            mountPassengers.addAll(mount.getPassengers());
            teleportOnce(mount, result::addAdditionalResult);
        }

        final List<Entity> teleportedLeashedEntities = new ArrayList<>();
        for (final var leashedEntity : context.getLeashedEntities()) {
            final var leashedResult = teleportOnce(leashedEntity, result::addAdditionalResult);
            if (leashedResult.isSuccessful()) {
                teleportedLeashedEntities.add(leashedResult.entity());
            }
        }

        var teleportedEntity = teleportOnce(entity, null);
        if (mount != null && teleportedEntity.isSuccessful()) {
            teleportedEntity = teleportedEntity.withOriginalVehicle(mount, mountPassengers.indexOf(entity));
            resultsByOriginalEntity.put(entity, teleportedEntity);
        }

        for (int i = 0; i < mountPassengers.size(); i++) {
            final var passenger = mountPassengers.get(i);
            if (passenger != entity) {
                var teleportedPassenger = teleportOnce(passenger, null);
                if (teleportedPassenger.isSuccessful()) {
                    teleportedPassenger = teleportedPassenger.withOriginalVehicle(mount, i);
                }
                resultsByOriginalEntity.put(passenger, teleportedPassenger);
                result.addAdditionalResult(teleportedPassenger);
            }
        }

        if (entity == context.getEntity()) {
            result.setPrimaryResult(teleportedEntity);
        } else {
            result.addAdditionalResult(teleportedEntity);
        }

        // We have to update the leashedToEntity in case the player was cloned during dimensional teleport
        if (teleportedEntity.isSuccessful()) {
            final var leashHolder = teleportedEntity.entity();
            teleportedLeashedEntities.forEach(teleportedLeashedEntity -> {
                if (teleportedLeashedEntity instanceof Mob teleportedLeashedMob) {
                    teleportedLeashedMob.setLeashedTo(leashHolder, true);
                }
            });
        }
    }

    private EntityTeleportResult teleportOnce(Entity entity, @Nullable Consumer<EntityTeleportResult> resultConsumer) {
        var teleportResult = resultsByOriginalEntity.get(entity);
        if (teleportResult == null) {
            teleportResult = WaystoneTeleportManager.teleportEntity(context, entity, destination);
            resultsByOriginalEntity.put(entity, teleportResult);
            if (resultConsumer != null) {
                resultConsumer.accept(teleportResult);
            }
        }

        return teleportResult;
    }

    public void restoreMounts() {
        final var passengerResultsByMount = new IdentityHashMap<Entity, List<EntityTeleportResult>>();
        for (final var teleportResult : resultsByOriginalEntity.values()) {
            if (!teleportResult.isSuccessful()) {
                continue;
            }

            teleportResult.originalVehicle()
                    .map(resultsByOriginalEntity::get)
                    .filter(EntityTeleportResult::isSuccessful)
                    .ifPresent(mountResult -> passengerResultsByMount
                            .computeIfAbsent(mountResult.entity(), it -> new ArrayList<>())
                            .add(teleportResult));
        }

        passengerResultsByMount.forEach((mount, passengerResults) -> {
            passengerResults.sort(Comparator.comparingInt(EntityTeleportResult::passengerIndex));
            final var desiredPassengers = new ArrayList<Entity>();
            for (final var passengerResult : passengerResults) {
                final var passenger = passengerResult.entity();
                if (passenger.isAlive() && passenger.level() == mount.level() && !desiredPassengers.contains(passenger)) {
                    desiredPassengers.add(passenger);
                }
            }

            mount.ejectPassengers();
            desiredPassengers.forEach(passenger -> passenger.startRiding(mount, true, false));
        });
    }

}
