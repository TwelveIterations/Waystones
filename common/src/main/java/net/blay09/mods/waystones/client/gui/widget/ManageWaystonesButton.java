package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class ManageWaystonesButton extends Button.Plain {

    private static final Component LABEL = Component.translatable("container.waystones.manage_waystones");
    private static final Identifier ICON = id("waystone_selection/manage");

    public ManageWaystonesButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, Component.empty(), pressable, _ -> Component.translatable("container.waystones.manage_waystones"));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON, getX() + 2, getY() + 2, 16, 16);
        if (isHovered) {
            guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, LABEL, mouseX, mouseY, null);
        }
    }
}
