package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class WaystoneGroupButton extends Button {

    private static final int TEXT_MARGIN = 4;

    private @Nullable WaystoneGroup group;
    private final OnPress backwardsPressable;

    public WaystoneGroupButton(int x, int y, int width, @Nullable WaystoneGroup group, Component emptyMessage, OnPress pressable, OnPress backwardsPressable) {
        super(x, y, width, 20, group != null ? coloredGroupName(group) : emptyMessage, pressable, Button.DEFAULT_NARRATION);
        this.group = group;
        this.backwardsPressable = backwardsPressable;
    }

    public void setGroup(@Nullable WaystoneGroup group, Component emptyMessage) {
        this.group = group;
        setMessage(group != null ? coloredGroupName(group) : emptyMessage);
    }

    private static Component coloredGroupName(WaystoneGroup group) {
        return group.name().copy().withColor(group.color());
    }

    private static int groupTextColor(WaystoneGroup group) {
        return 0xFF000000 | (group.color() & 0x00FFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && visible && button == 1 && isMouseOver(mouseX, mouseY)) {
            backwardsPressable.onPress(this);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        final var message = getMessage();
        setMessage(Component.empty());
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        setMessage(message);

        final var font = Minecraft.getInstance().font;
        final int labelLeft = getX() + TEXT_MARGIN + (group != null ? 18 : 0);
        final int maxWidth = getX() + getWidth() - TEXT_MARGIN - labelLeft;
        final var label = font.plainSubstrByWidth(message.getString(), maxWidth);
        final int color = group != null ? groupTextColor(group) : 0xFFFFFF;

        if (group != null) {
            graphics.blitSprite(WaystoneGroupIcons.getIcon(group), getX() + 3, getY() + 2, 16, 16);
        }

        graphics.drawString(font, label, labelLeft, getY() + 6, color, true);
    }
}
