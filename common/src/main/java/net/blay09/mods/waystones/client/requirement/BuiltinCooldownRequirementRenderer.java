package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.CooldownRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class BuiltinCooldownRequirementRenderer implements RequirementRenderer<CooldownRequirement> {
    @Override
    public void renderWidget(Player player, CooldownRequirement requirement, GuiGraphicsExtractor guiGraphics, int x, int y) {
        final var timeLeftStr = BuiltinCooldownRenderUtils.formatTimeLeft(requirement);
        if (timeLeftStr == null) {
            return;
        }

        final var font = Minecraft.getInstance().font;
        guiGraphics.text(font, timeLeftStr, x + 1, y + font.lineHeight / 2, 0xFFFFAAAA);
    }

    @Override
    public int getWidth(Player player, CooldownRequirement requirement) {
        final var timeLeftStr = BuiltinCooldownRenderUtils.formatTimeLeft(requirement);
        if (timeLeftStr == null) {
            return 0;
        }

        final var font = Minecraft.getInstance().font;
        return font.width(timeLeftStr) + 1;
    }

    @Override
    public void appendHoverText(Player player, CooldownRequirement requirement, List<Component> tooltip) {
        final long millisLeft = BuiltinCooldownRenderUtils.getMillisLeft(requirement);
        if (millisLeft > 0) {
            tooltip.add(Component.translatable("tooltip.waystones.cooldown_left", millisLeft / 1000).withStyle(ChatFormatting.GOLD));
        }
    }
}
