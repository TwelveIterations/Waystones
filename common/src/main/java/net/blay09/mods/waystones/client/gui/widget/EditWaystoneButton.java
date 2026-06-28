package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class EditWaystoneButton extends Button {

    private static final ResourceLocation SPRITE = id("widgets/alias");

    private final int visibleRegionStart;
    private final int visibleRegionHeight;

    public EditWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, Component message, OnPress pressable) {
        super(x,
                y,
                18,
                18,
                message,
                pressable,
                Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            if (isHovered) {
                guiGraphics.setColor(1f, 1f, 1f, 1f);
            } else {
                guiGraphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            guiGraphics.blitSprite(SPRITE, getX(), getY(), 13, 13);
            guiGraphics.setColor(1f, 1f, 1f, 1f);
        }
    }
}
