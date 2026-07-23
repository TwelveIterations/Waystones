package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ColorButton extends Button {

    private GroupColor color;

    public ColorButton(int x, int y, int color) {
        super(x, y, 21, 21, Component.empty(), button -> ((ColorButton) button).cycleColor(), Button.DEFAULT_NARRATION);
        this.color = GroupColor.fromArgb(color);
        updateTooltip();
    }

    public int getColor() {
        return color.toArgb();
    }

    private void cycleColor() {
        final var colors = GroupColor.COLORS;
        color = colors.get((GroupColor.indexOf(color) + 1) % colors.size());
        updateTooltip();
    }

    private void cycleColorBackwards() {
        final var colors = GroupColor.COLORS;
        color = colors.get((GroupColor.indexOf(color) + colors.size() - 1) % colors.size());
        updateTooltip();
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(Component.translatable("gui.waystones.group_settings.color", color.getName())));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && active && visible && isMouseOver(event.x(), event.y())) {
            cycleColorBackwards();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(guiGraphics);
        guiGraphics.fill(getX() + 5, getY() + 5, getX() + 16, getY() + 16, getColor());
        guiGraphics.outline(getX() + 4, getY() + 4, 13, 13, 0xFF000000);
    }
}
