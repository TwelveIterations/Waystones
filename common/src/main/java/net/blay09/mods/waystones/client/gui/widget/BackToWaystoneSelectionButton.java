package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class BackToWaystoneSelectionButton extends Button {

    private static final Component LABEL = Component.translatable("gui.waystones.waystone_selection.back");
    private static final ResourceLocation ICON = id("widgets/back");

    public BackToWaystoneSelectionButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        setTooltip(Tooltip.create(LABEL));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blitSprite(ICON, getX() + 2, getY() + 2, 16, 16);
    }
}
