package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;

public class ManageWaystoneGroupButton extends Button.Plain {

    private final WaystoneGroup group;

    public ManageWaystoneGroupButton(int width, WaystoneGroup group) {
        super(0, 0, width, 20, group.name(), _ -> {
        }, Button.DEFAULT_NARRATION);
        this.group = group;
        active = false;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(graphics);

        final int iconLeft = getX() + 3;
        final int iconTop = getY() + 2;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, group.icon(), iconLeft, iconTop, 16, 16);

        final int labelLeft = getX() + TEXT_MARGIN + 18;
        final int labelRight = getX() + getWidth() - TEXT_MARGIN;
        final int labelTop = getY();
        final int labelBottom = getY() + getHeight();
        final var buttonTextOutput = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        buttonTextOutput.acceptScrolling(message, (labelLeft + labelRight) / 2, labelLeft, labelRight, labelTop, labelBottom);
    }

}
