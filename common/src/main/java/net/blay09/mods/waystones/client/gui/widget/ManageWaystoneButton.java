package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ManageWaystoneButton extends AbstractWaystoneButton {

    public ManageWaystoneButton(int width, Waystone waystone) {
        super(0, 0, width, waystone, _ -> {
        });
        active = false;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(graphics);

        final int rightPadding = renderDimensionOverlay(graphics);
        final int labelLeft = getX() + TEXT_MARGIN;
        final int labelRight = getX() + getWidth() - TEXT_MARGIN - rightPadding;
        final int labelTop = getY();
        final int labelBottom = getY() + getHeight();
        final var buttonTextOutput = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        buttonTextOutput.acceptScrolling(message, getX() + getWidth() / 2, labelLeft, labelRight, labelTop, labelBottom);
    }
}
