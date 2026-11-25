package net.blay09.mods.waystones.config.rules;

import net.blay09.mods.shogi.context.executor.EffectExecutor;

import java.util.function.UnaryOperator;

public class WaystonesEffectExecutors {
    private static UnaryOperator<EffectExecutor> configurationOverrides = UnaryOperator.identity();

    public static void setConfigurationOverrides(UnaryOperator<EffectExecutor> overrides) {
        configurationOverrides = overrides;
    }

    public static EffectExecutor applyConfigurationOverrides(EffectExecutor executor) {
        return configurationOverrides.apply(executor);
    }

    public static EffectExecutor deferred() {
        return applyConfigurationOverrides(EffectExecutor.deferred());
    }

    public static EffectExecutor simulated() {
        return applyConfigurationOverrides(EffectExecutor.simulated());
    }
}
