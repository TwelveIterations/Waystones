package net.blay09.mods.waystones.compat.shogi;

import net.blay09.mods.shogi.common.effect.cost.*;
import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.blay09.mods.waystones.client.requirement.*;
import java.util.Optional;

import static net.blay09.mods.waystones.client.requirement.RequirementClientRegistry.registerRenderer;
import static net.blay09.mods.waystones.client.requirement.RequirementClientRegistry.registerMerger;

public class ShogiClientIntegration {
    public ShogiClientIntegration() {
        registerRenderer(CooldownInformation.class, new CooldownRequirementRenderer());
        registerRenderer(CooldownInformation.class, new SoftCooldownRequirementRenderer());
        registerRenderer(ExperienceLevelCostInformation.class, new ExperienceLevelRequirementRenderer());
        registerRenderer(ExperiencePointsCostInformation.class, new ExperiencePointsRequirementRenderer());
        registerRenderer(ItemCostInformation.class, new ItemRequirementRenderer());
        registerRenderer(RefusalInformation.class, new RefuseRequirementRenderer());
        registerMerger(ExperienceLevelCostInformation.class, (current, incoming) -> Optional.of(new ExperienceLevelCostInformation(
                Math.min(current.available(), incoming.available()),
                current.required() + incoming.required()
        )));
        registerMerger(ExperiencePointsCostInformation.class, (current, incoming) -> Optional.of(new ExperiencePointsCostInformation(
                Math.min(current.available(), incoming.available()),
                current.required() + incoming.required()
        )));
        registerMerger(ItemCostInformation.class, (current, incoming) -> {
            if (!current.item().equals(incoming.item())) {
                return Optional.empty();
            }
            return Optional.of(new ItemCostInformation(
                    current.item(),
                    Math.min(current.available(), incoming.available()),
                    current.required() + incoming.required()
            ));
        });
    }
}
