package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.failure.RefusalInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RefuseRequirementRenderer implements RequirementRenderer<RefusalInformation> {

    private static final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");

    @Override
    public void renderWidget(Player player, RefusalInformation requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, CANCEL_SPRITE, x, y, 16, 16, 0x80FFFFFF);
    }

    @Override
    public void appendHoverText(Player player, RefusalInformation requirement, List<Component> tooltip) {
        tooltip.add(requirement.message().copy().withStyle(ChatFormatting.RED));
    }
}
