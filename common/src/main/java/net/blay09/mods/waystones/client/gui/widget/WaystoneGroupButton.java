package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class WaystoneGroupButton extends Button.Plain {

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

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (active && visible && event.button() == 1 && isMouseOver(event.x(), event.y())) {
            backwardsPressable.onPress(this);
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(graphics);

        if (group != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WaystoneGroupIcons.getIcon(group), getX() + 3, getY() + 2, 16, 16);
        }

        final int labelLeft = getX() + TEXT_MARGIN + (group != null ? 18 : 0);
        final int labelRight = getX() + getWidth() - TEXT_MARGIN;
        final int labelTop = getY();
        final int labelBottom = getY() + getHeight();
        final var buttonTextOutput = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        buttonTextOutput.acceptScrolling(message, (labelLeft + labelRight) / 2, labelLeft, labelRight, labelTop, labelBottom);
    }
}
