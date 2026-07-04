package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WaystoneTeleportResult {
    private @Nullable EntityTeleportResult primaryResult;
    private final List<EntityTeleportResult> additionalResults;
    private @Nullable WaystoneTeleportError teleportError;

    public WaystoneTeleportResult(List<EntityTeleportResult> additionalResults) {
        this.additionalResults = additionalResults;
    }

    public static WaystoneTeleportResult failed(WaystoneTeleportError error) {
        final var result = new WaystoneTeleportResult(Collections.emptyList());
        result.setTeleportError(error);
        return result;
    }

    public Optional<EntityTeleportResult> primaryResult() {
        return Optional.ofNullable(primaryResult);
    }

    public List<EntityTeleportResult> additionalResults() {
        return Collections.unmodifiableList(additionalResults);
    }

    public List<EntityTeleportResult> results() {
        final var results = new ArrayList<EntityTeleportResult>(additionalResults.size() + 1);
        if (primaryResult != null) {
            results.add(primaryResult);
        }

        results.addAll(additionalResults);
        return results;
    }

    public List<Entity> teleportedEntities() {
        return results().stream()
                .filter(EntityTeleportResult::isSuccessful)
                .map(EntityTeleportResult::entity)
                .toList();
    }

    public Optional<WaystoneTeleportError> teleportError() {
        return Optional.ofNullable(teleportError);
    }

    public Optional<WaystoneTeleportError> error() {
        return teleportError().or(() -> primaryResult().flatMap(it -> Optional.ofNullable(it.error())));
    }

    public boolean isSuccessful() {
        return teleportError == null && primaryResult().map(EntityTeleportResult::isSuccessful).orElse(true);
    }

    public void setPrimaryResult(EntityTeleportResult result) {
        primaryResult = result;
    }

    public void addAdditionalResult(EntityTeleportResult result) {
        additionalResults.add(result);
    }

    public void setTeleportError(WaystoneTeleportError error) {
        this.teleportError = error;
    }
}
