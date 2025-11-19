package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.ExperienceLevelRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class ExperienceLevelRequirementRenderer implements RequirementRenderer<ExperienceLevelRequirement> {

    private static final Identifier[] ENABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3")};
    private static final Identifier[] DISABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3_disabled")};

    @Override
    public void renderWidget(Player player, ExperienceLevelRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var levels = requirement.getLevels();
        if (levels > 0) {
            final var canAfford = requirement.canAfford(player);
            final var spriteIndex = Math.max(0, Math.min(levels, 3) - 1);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, canAfford ? ENABLED_LEVEL_SPRITES[spriteIndex] : DISABLED_LEVEL_SPRITES[spriteIndex], x, y, 16, 16);

            final var font = Minecraft.getInstance().font;
            if (levels > 3) {
                guiGraphics.drawString(font, "+", x + 15, y + 4, 0xFFC8FF8F);
            }
        }
    }
}
