package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.CooldownRequirement;
import net.blay09.mods.waystones.config.WaystonesConfig;
import org.jspecify.annotations.Nullable;

public final class BuiltinCooldownRenderUtils {
    private static final long NANOSECONDS_PER_MILLISECOND = 1_000_000L;

    private BuiltinCooldownRenderUtils() {
    }

    public static long getMillisLeft(CooldownRequirement requirement) {
        if (!WaystonesConfig.getActive().rules.enableCooldowns) {
            return 0;
        }

        if (requirement.nanosecondsPerTick() <= 0L) {
            return 0;
        }

        final long totalNanos = Math.max(0L, requirement.remainingTicks()) * requirement.nanosecondsPerTick();
        final long elapsedMillis = Math.max(0L, System.currentTimeMillis() - requirement.nowUnixMs());
        final long elapsedNanos = elapsedMillis * NANOSECONDS_PER_MILLISECOND;
        final long nanosLeft = totalNanos - elapsedNanos;
        if (nanosLeft <= 0) {
            return 0;
        }

        return nanosLeft / NANOSECONDS_PER_MILLISECOND;
    }

    public static @Nullable String formatTimeLeft(CooldownRequirement requirement) {
        if (!WaystonesConfig.getActive().rules.enableCooldowns) {
            return null;
        }

        final long millisLeft = getMillisLeft(requirement);
        if (millisLeft <= 0L) {
            return null;
        }

        long secondsLeft = millisLeft / 1000L;
        final long minutesLeft = secondsLeft / 60L;
        secondsLeft %= 60L;
        return String.format("%02d:%02d", minutesLeft, secondsLeft);
    }
}
