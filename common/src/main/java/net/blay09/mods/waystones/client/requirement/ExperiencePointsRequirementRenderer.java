package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.cost.ExperiencePointsCostInformation;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.util.ExperienceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ExperiencePointsRequirementRenderer implements RequirementRenderer<ExperiencePointsCostInformation> {

    private static final Identifier[] ENABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3")};
    private static final Identifier[] DISABLED_LEVEL_SPRITES = new Identifier[]{
            Identifier.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
            Identifier.withDefaultNamespace("container/enchanting_table/level_3_disabled")};

    @Override
    public void renderWidget(Player player, ExperiencePointsCostInformation requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var points = WaystonesConfig.getActive().rules.enableXpCosts ? requirement.required() : 0;
        if (points > 0) {
            final var levels = ExperienceUtils.calculateDisplayedLevelCostFromExperiencePoints(player.experienceLevel, requirement.available(), points);
            final var canAfford = requirement.available() >= points;
            final var spriteIndex = Math.max(0, Math.min(levels, 3) - 1);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, canAfford ? ENABLED_LEVEL_SPRITES[spriteIndex] : DISABLED_LEVEL_SPRITES[spriteIndex], x, y, 16, 16);

            final var font = Minecraft.getInstance().font;
            if (levels > 3) {
                guiGraphics.text(font, "+", x + 15, y + 4, 0xFFC8FF8F);
            }
        }
    }

    @Override
    public void appendHoverText(Player player, ExperiencePointsCostInformation requirement, List<Component> tooltip) {
        final var points = WaystonesConfig.getActive().rules.enableXpCosts ? requirement.required() : 0;
        final var levels = ExperienceUtils.calculateDisplayedLevelCostFromExperiencePoints(player.experienceLevel, requirement.available(), points);
        if (levels > 0) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.level_requirement", levels).withStyle(ChatFormatting.GREEN));
        } else if (points > 0) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.xp_requirement", points).withStyle(ChatFormatting.GREEN));
        }
    }
}
