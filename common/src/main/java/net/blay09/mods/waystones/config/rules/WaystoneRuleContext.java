package net.blay09.mods.waystones.config.rules;

import net.blay09.mods.shogi.context.MutableShogiContext;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class WaystoneRuleContext {
    private static final String WAYSTONE_VARIABLE = "waystone";

    private WaystoneRuleContext() {
    }

    @SuppressWarnings("unchecked")
    public static Optional<Waystone> getEffectiveWaystone(ShogiContext context, WaystoneTeleportContext waystoneTeleportContext) {
        final var variable = context.getVariable(WAYSTONE_VARIABLE);
        if (variable.isPresent()) {
            final var value = variable.get();
            if (value instanceof Waystone waystone) {
                return Optional.of(waystone);
            } else if (value instanceof Optional<?> optional) {
                return (Optional<Waystone>) optional;
            }
        }
        return Optional.of(waystoneTeleportContext.getTargetWaystone());
    }

    public static void setEffectiveWaystone(MutableShogiContext nestedContext, @Nullable Waystone waystone) {
        nestedContext.withVariable(WaystoneRuleContext.WAYSTONE_VARIABLE, Optional.ofNullable(waystone));
    }
}
