package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class EditWaystoneButton extends Button {

    private static final Identifier SPRITE = id("widgets/alias");

    private final int visibleRegionStart;
    private final int visibleRegionHeight;

    public EditWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, OnPress pressable) {
        super(x,
                y,
                18,
                18,
                Component.translatable("gui.waystones.waystone_selection.edit_personal_settings"),
                pressable,
                Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    SPRITE,
                    getX(),
                    getY(),
                    13,
                    13,
                    isHovered ? 0xFFFFFFFF : 0x80808080);
        }
    }
}
