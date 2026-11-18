package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class BackToWaystoneSelectionButton extends Button.Plain {

    private static final Component LABEL = Component.translatable("gui.waystones.waystone_selection.back");
    private static final Identifier ICON = id("widgets/back");

    public BackToWaystoneSelectionButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, Component.empty(), pressable, _ -> LABEL.copy());
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics) {
        super.extractContents(graphics);
        graphics.blitSprite(GuiPipeline.TEXTURED, ICON, getX() + 2, getY() + 2, 16, 16);
        if (isHovered) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, LABEL, graphics.mouseX(), graphics.mouseY(), null);
        }
    }
}
