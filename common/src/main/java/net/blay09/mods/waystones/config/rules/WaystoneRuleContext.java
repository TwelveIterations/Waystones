package net.blay09.mods.waystones.config.rules;

import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class WaystoneRuleContext {
    public static final String WAYSTONE_VARIABLE = "waystone";
    public static final String SOURCE_WAYSTONE_VARIABLE = "source_waystone";
    public static final String TARGET_WAYSTONE_VARIABLE = "target_waystone";
    public static final String FLAGS_VARIABLE = "flags";

    private WaystoneRuleContext() {
    }

    @SuppressWarnings("unchecked")
    public static Optional<Waystone> getEffectiveWaystone(ShogiContext context) {
        final var variable = context.getVariable(WAYSTONE_VARIABLE);
        if (variable.isPresent()) {
            final var value = variable.get();
            if (value instanceof Waystone waystone) {
                return Optional.of(waystone);
            } else if (value instanceof Optional<?> optional) {
                return (Optional<Waystone>) optional;
            }
        }
        final var targetWaystone = getTargetWaystone(context);
        if (targetWaystone.isPresent()) {
            return targetWaystone;
        }

        if (context.blockEntity() instanceof WaystoneBlockEntityBase waystoneBlockEntityBase) {
            final var waystone = waystoneBlockEntityBase.getWaystone();
            if (waystone.isValid()) {
                return Optional.of(waystoneBlockEntityBase.getWaystone());
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Optional<Waystone> getSourceWaystone(ShogiContext context) {
        final var variable = context.getVariable(SOURCE_WAYSTONE_VARIABLE);
        if (variable.isPresent() && variable.get() instanceof Optional<?> optional) {
            return (Optional<Waystone>) optional;
        }
        return Optional.empty();
    }

    public static Optional<Waystone> getTargetWaystone(ShogiContext context) {
        final var variable = context.getVariable(TARGET_WAYSTONE_VARIABLE);
        if (variable.isPresent() && variable.get() instanceof Waystone waystone) {
            return Optional.of(waystone);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Set<Identifier> getFlags(ShogiContext context) {
        final var variable = context.getVariable(FLAGS_VARIABLE);
        if (variable.isPresent() && variable.get() instanceof Set<?> flags) {
            return (Set<Identifier>) flags;
        }
        return Set.of();
    }

    public static boolean isDimensionalTeleport(ShogiContext context) {
        final var targetDimension = getTargetWaystone(context).map(Waystone::getDimension);
        if (targetDimension.isEmpty()) {
            return false;
        }
        final var sourceDimension = getSourceWaystone(context)
                .map(Waystone::getDimension)
                .orElse(context.requireLevel().dimension());
        return targetDimension.get() != sourceDimension;
    }

    public static void setEffectiveWaystone(MutableShogiContext nestedContext, @Nullable Waystone waystone) {
        nestedContext.withVariable(WAYSTONE_VARIABLE, Optional.ofNullable(waystone));
    }
}
