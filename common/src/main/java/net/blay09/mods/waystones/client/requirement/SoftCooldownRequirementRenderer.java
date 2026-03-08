package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.server.cooldown.CooldownInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SoftCooldownRequirementRenderer implements RequirementRenderer<CooldownInformation> {
    @Override
    public void renderWidget(Player player, CooldownInformation requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var timeLeftStr = CooldownRenderUtils.formatTimeLeft(requirement);
        if (timeLeftStr == null) return;
        final var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, timeLeftStr, x + 1, y + font.lineHeight / 2, 0xFFFFAAAA);
    }

    @Override
    public int getWidth(Player player, CooldownInformation requirement) {
        final var timeLeftStr = CooldownRenderUtils.formatTimeLeft(requirement);
        if (timeLeftStr == null) return 0;
        final var font = Minecraft.getInstance().font;
        return font.width(timeLeftStr) + 1;
    }

    @Override
    public void appendHoverText(Player player, CooldownInformation requirement, List<Component> tooltip) {
        final long millisLeft = CooldownRenderUtils.getMillisLeft(requirement);
        if (millisLeft > 0) {
            tooltip.add(Component.translatable("tooltip.waystones.cooldown_left", millisLeft / 1000).withStyle(ChatFormatting.GOLD));
        }
    }
}
