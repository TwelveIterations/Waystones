package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class RemoveWaystoneGroupButton extends Button {

    private static final ResourceLocation SPRITE = id("widgets/delete");

    private final Tooltip tooltip;
    private final Tooltip activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneGroupButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, OnPress pressable) {
        super(x, y, 18, 18, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        tooltip = Tooltip.create(Component.translatable("gui.waystones.waystone_selection.hold_shift_to_delete"));
        activeTooltip = Tooltip.create(Component.translatable("gui.waystones.waystone_selection.click_to_delete"));
        setTooltip(tooltip);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            shiftGuard = true;
            return true;
        }

        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        boolean shiftDown = Screen.hasShiftDown();
        if (!shiftDown) {
            shiftGuard = false;
        }
        active = !shiftGuard && shiftDown;
        setTooltip(active ? activeTooltip : tooltip);

        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            final var alpha = isHovered && active ? 1f : 0.5f;
            guiGraphics.setColor(alpha, alpha, alpha, alpha);
            guiGraphics.blitSprite(SPRITE, getX(), getY(), 13, 13);
            guiGraphics.setColor(1f, 1f, 1f, 1f);
        }
    }
}
