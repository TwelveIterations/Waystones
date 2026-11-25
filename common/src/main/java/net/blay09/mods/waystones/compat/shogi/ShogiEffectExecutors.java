package net.blay09.mods.waystones.compat.shogi;

import net.blay09.mods.shogi.common.effect.cost.DamageItem;
import net.blay09.mods.shogi.common.effect.cost.ExperienceLevelCost;
import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCost;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownCost;
import net.blay09.mods.shogi.context.executor.EffectExecutor;
import net.blay09.mods.waystones.config.WaystonesConfig;

public class ShogiEffectExecutors {

    public static EffectExecutor applyConfigurationOverrides(EffectExecutor executor) {
        executor.overrideConsume(DamageItem.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableDurability) {
                operation.accept(value);
            }
        });

        executor.overrideAggregate(ExperienceLevelCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableXpCosts) {
                return operation.apply(value);
            }
            return 0;
        });

        executor.overrideConsume(ExperienceLevelCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableXpCosts) {
                operation.accept(value);
            }
        });

        executor.overrideAggregate(ExperiencePointsCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableXpCosts) {
                return operation.apply(value);
            }
            return 0;
        });

        executor.overrideConsume(ExperiencePointsCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableXpCosts) {
                operation.accept(value);
            }
        });

        executor.overrideConsume(CooldownCost.IDENTIFIER, (operation, value) -> {
            if (WaystonesConfig.getActive().costs.enableCooldowns) {
                operation.accept(value);
            }
        });
        return executor;
    }

}
