package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ManageWaystoneGroupButton extends Button {

    private static final int TEXT_MARGIN = 4;

    private final WaystoneGroup group;

    public ManageWaystoneGroupButton(int width, WaystoneGroup group) {
        super(0, 0, width, 20, coloredGroupName(group), button -> {
        }, Button.DEFAULT_NARRATION);
        this.group = group;
        active = false;
    }

    private static Component coloredGroupName(WaystoneGroup group) {
        return group.name().copy().withColor(group.color() & 0x00FFFFFF);
    }

    private static int groupTextColor(WaystoneGroup group) {
        return 0xFF000000 | (group.color() & 0x00FFFFFF);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);

        final int iconLeft = getX() + 3;
        final int iconTop = getY() + 2;
        graphics.blitSprite(group.icon(), iconLeft, iconTop, 16, 16);

        final var font = Minecraft.getInstance().font;
        final int labelLeft = getX() + TEXT_MARGIN + 18;
        final int maxWidth = getX() + getWidth() - TEXT_MARGIN - labelLeft;
        final var label = font.plainSubstrByWidth(group.name().getString(), maxWidth);
        graphics.drawString(font, label, labelLeft, getY() + 6, groupTextColor(group), true);
    }
}
