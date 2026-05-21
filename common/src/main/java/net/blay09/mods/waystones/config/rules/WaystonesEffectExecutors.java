package net.blay09.mods.waystones.config.rules;

import net.blay09.mods.shogi.common.effect.cost.DamageItem;
import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCost;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCost;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownCost;
import net.blay09.mods.shogi.context.executor.EffectExecutor;
import net.blay09.mods.waystones.config.WaystonesConfig;

public class WaystonesEffectExecutors {

    public static EffectExecutor applyConfigurationOverrides(EffectExecutor executor) {
        executor.overrideConsume(DamageItem.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableDurability) {
                operation.accept(value);
            }
        });

        executor.overrideAggregate(ExperienceLevelCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableXpCosts) {
                return operation.apply(value);
            }
            return 0;
        });

        executor.overrideConsume(ExperienceLevelCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableXpCosts) {
                operation.accept(value);
            }
        });

        executor.overrideAggregate(ExperiencePointsCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableXpCosts) {
                return operation.apply(value);
            }
            return 0;
        });

        executor.overrideConsume(ExperiencePointsCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableXpCosts) {
                operation.accept(value);
            }
        });

        executor.overrideConsume(CooldownCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().rules.enableCooldowns) {
                operation.accept(value);
            }
        });
        return executor;
    }

    public static EffectExecutor deferred() {
        return applyConfigurationOverrides(EffectExecutor.deferred());
    }

    public static EffectExecutor simulated() {
        return applyConfigurationOverrides(EffectExecutor.simulated());
    }
}
