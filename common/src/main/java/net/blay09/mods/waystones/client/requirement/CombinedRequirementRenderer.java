package net.blay09.mods.waystones.client.requirement;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class CombinedRequirementRenderer implements RequirementRenderer<List<Object>> {
    private final boolean renderFallbacks;

    public CombinedRequirementRenderer(boolean renderFallbacks) {
        this.renderFallbacks = renderFallbacks;
    }

    @Override
    public void renderWidget(Player player, List<Object> requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var mergedRequirements = RequirementClientRegistry.mergeRequirements(requirement);
        final var sortedChildren = mergedRequirements
                .stream()
                .map(it -> Pair.of(it, getRenderer(it)))
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
        return RequirementClientRegistry.mergeRequirements(requirement)
                .stream()
                .map(it -> Pair.of(it, getRenderer(it)))
                .filter(it -> it.getSecond() != null)
                .mapToInt(it -> it.getSecond().getWidth(player, it.getFirst()))
                .sum();
    }

    @Override
    public void appendHoverText(Player player, List<Object> requirement, List<Component> tooltip) {
        RequirementClientRegistry.mergeRequirements(requirement)
                .stream()
                .map(it -> Pair.of(it, getRenderer(it)))
                .filter(it -> it.getSecond() != null)
                .sorted(Comparator.comparingInt(it -> it.getSecond().getOrder()))
                .forEach(it -> it.getSecond().appendHoverText(player, it.getFirst(), tooltip));
    }

    private RequirementRenderer<Object> getRenderer(Object requirement) {
        return renderFallbacks ? RequirementClientRegistry.getErrorRenderer(requirement) : RequirementClientRegistry.getRenderer(requirement);
    }
}
