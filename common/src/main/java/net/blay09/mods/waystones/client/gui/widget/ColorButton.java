package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

public class ColorButton extends Button {

    private static final DyeColor[] COLORS = DyeColor.values();
    private DyeColor color;

    public ColorButton(int x, int y, int color) {
        super(x, y, 21, 21, Component.empty(), button -> ((ColorButton) button).cycleColor(), Button.DEFAULT_NARRATION);
        this.color = fromArgb(color);
        updateTooltip();
    }

    public int getColor() {
        return toArgb(color);
    }

    public Component getColorName() {
        return Component.translatable("color.minecraft." + color.getName());
    }

    private void cycleColor() {
        color = COLORS[(color.ordinal() + 1) % COLORS.length];
        updateTooltip();
    }

    private void cycleColorBackwards() {
        color = COLORS[(color.ordinal() + COLORS.length - 1) % COLORS.length];
        updateTooltip();
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(getColorName()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && active && visible && isMouseOver(mouseX, mouseY)) {
            cycleColorBackwards();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.fill(getX() + 5, getY() + 5, getX() + 16, getY() + 16, getColor());
        guiGraphics.fill(getX() + 4, getY() + 4, getX() + 17, getY() + 5, 0xFF000000);
        guiGraphics.fill(getX() + 4, getY() + 16, getX() + 17, getY() + 17, 0xFF000000);
        guiGraphics.fill(getX() + 4, getY() + 4, getX() + 5, getY() + 17, 0xFF000000);
        guiGraphics.fill(getX() + 16, getY() + 4, getX() + 17, getY() + 17, 0xFF000000);
    }

    public static int toArgb(DyeColor color) {
        return color.getTextColor() | 0xFF000000;
    }

    private static DyeColor fromArgb(int color) {
        final var rgb = color & 0x00FFFFFF;
        for (final var dyeColor : COLORS) {
            if ((dyeColor.getTextColor() & 0x00FFFFFF) == rgb) {
                return dyeColor;
            }
        }

        return DyeColor.WHITE;
    }
}
