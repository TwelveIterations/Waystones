package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.SoftCooldownRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SoftCooldownRequirementRenderer implements RequirementRenderer<SoftCooldownRequirement> {
    @Override
    public void renderWidget(Player player, SoftCooldownRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var timeLeftStr = formatTimeLeft(player, requirement);
        if (timeLeftStr == null) return;
        final var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, timeLeftStr, x + 1, y + font.lineHeight / 2, 0xFFFFAAAA);
    }

    @Override
    public int getWidth(Player player, SoftCooldownRequirement requirement) {
        final var timeLeftStr = formatTimeLeft(player, requirement);
        if (timeLeftStr == null) return 0;
        final var font = Minecraft.getInstance().font;
        return font.width(timeLeftStr) + 1;
    }

    private static @Nullable String formatTimeLeft(Player player, SoftCooldownRequirement requirement) {
        final var millisLeft = requirement.getCooldownMillisLeft(player);
        if (millisLeft <= 0) {
            return null;
        }

        var secondsLeft = millisLeft / 1000;
        var minutesLeft = secondsLeft / 60;
        secondsLeft %= 60;
        return String.format("%02d:%02d", minutesLeft, secondsLeft);
    }
}