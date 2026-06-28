package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.client.gui.GuiGraphics;

public class ManageWaystoneButton extends AbstractWaystoneButton {

    public ManageWaystoneButton(int width, Waystone waystone) {
        super(0, 0, width, waystone, button -> {
        });
        active = false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        renderDimensionOverlay(guiGraphics);
    }
}
