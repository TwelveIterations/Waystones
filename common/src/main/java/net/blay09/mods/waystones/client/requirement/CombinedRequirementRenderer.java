package net.blay09.mods.waystones.client.requirement;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class CombinedRequirementRenderer implements RequirementRenderer<List<Object>> {
    @Override
    public void renderWidget(Player player, List<Object> requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var sortedChildren = requirement
                .stream()
                .map(it -> Pair.of(it, RequirementClientRegistry.getRenderer(it)))
                .sorted(Comparator.comparingInt(it -> it.getSecond() != null ? it.getSecond().getOrder() : 100))
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
    public int getWidth(Player player, List<Object> requirement) {
        return requirement
                .stream()
                .map(it -> Pair.of(it, RequirementClientRegistry.getRenderer(it)))
                .filter(it -> it.getSecond() != null)
                .mapToInt(it -> it.getSecond().getWidth(player, it.getFirst()))
                .sum();
    }

}