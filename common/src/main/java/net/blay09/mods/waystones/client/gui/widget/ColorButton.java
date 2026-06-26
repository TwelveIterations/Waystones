package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

public class ColorButton extends Button {

    private static final DyeColor[] COLORS = {
            DyeColor.WHITE,
            DyeColor.ORANGE,
            DyeColor.MAGENTA,
            DyeColor.LIGHT_BLUE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.PINK,
            DyeColor.GRAY,
            DyeColor.LIGHT_GRAY,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.GREEN,
            DyeColor.RED
    };
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
        color = COLORS[(indexOf(color) + 1) % COLORS.length];
        updateTooltip();
    }

    private void cycleColorBackwards() {
        color = COLORS[(indexOf(color) + COLORS.length - 1) % COLORS.length];
        updateTooltip();
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(getColorName()));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1 && active && visible && isMouseOver(event.x(), event.y())) {
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

    private static int indexOf(DyeColor color) {
        for (int i = 0; i < COLORS.length; i++) {
            if (COLORS[i] == color) {
                return i;
            }
        }

        return 0;
    }
}
