package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.requirement.RefuseRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class RefuseRequirementRenderer implements RequirementRenderer<RefuseRequirement> {

    private static final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");

    @Override
    public void renderWidget(Player player, RefuseRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, CANCEL_SPRITE, x, y, 16, 16, 0x80FFFFFF);
    }
}
