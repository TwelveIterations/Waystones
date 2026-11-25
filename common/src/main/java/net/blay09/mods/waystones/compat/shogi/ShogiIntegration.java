package net.blay09.mods.waystones.compat.shogi;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.config.rules.WaystonesEffectExecutors;

import java.util.List;

public class ShogiIntegration {
    public ShogiIntegration() {
        final var scope = WaystonesRules.scope;
        scope.registerEffect(HasModifier.IDENTIFIER, HasModifier.mapCodec(scope), List.of("item", "count"));
        scope.registerEffect(ModifierCost.IDENTIFIER, ModifierCost.mapCodec(scope), List.of("item", "count"));
        WaystonesEffectExecutors.setConfigurationOverrides(ShogiEffectExecutors::applyConfigurationOverrides);
        ShogiRequirementComponentResolvers.registerDefaults();
        ShogiCommands.initialize(Balm.commands());
    }
}
