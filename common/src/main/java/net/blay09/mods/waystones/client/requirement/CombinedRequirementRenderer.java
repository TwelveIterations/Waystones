package net.blay09.mods.waystones.client.requirement;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.waystones.requirement.CombinedRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

public class CombinedRequirementRenderer implements RequirementRenderer<CombinedRequirement> {
    @Override
    public void renderWidget(Player player, CombinedRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var sortedChildren = requirement.getRequirements()
                .stream()
                .filter(it -> !it.isEmpty())
                .map(it -> Pair.of(it, RequirementClientRegistry.getRenderer(it)))
                .filter(it -> it.getSecond() != null)
                .sorted(Comparator.comparingInt(it -> it.getSecond().getOrder()))
                .toList();
        var currentX = x;
        for (final var child : sortedChildren) {
            final var childRenderer = child.getSecond();
            if (childRenderer != null) {
                childRenderer.renderWidget(player, child.getFirst(), guiGraphics, mouseX, mouseY, partialTicks, currentX, y);
                currentX += 2 + childRenderer.getWidth(player, child.getFirst());
            }
        }
    }

    @Override
    public int getWidth(Player player, CombinedRequirement requirement) {
        return requirement.getRequirements()
                .stream()
                .filter(it -> !it.isEmpty())
                .map(it -> Pair.of(it, RequirementClientRegistry.getRenderer(it)))
                .filter(it -> it.getSecond() != null)
                .mapToInt(it -> it.getSecond().getWidth(player, it.getFirst()))
                .sum();
    }

}