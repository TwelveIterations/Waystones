package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class RemoveWaystoneButton extends Button {

    private static final Identifier SPRITE = id("waystone_selection/delete");

    private final Tooltip tooltip;
    private final Tooltip activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, Waystone waystone, OnPress pressable) {
        super(x, y, 18, 18, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        var tooltipComponent = Component.translatable("gui.waystones.waystone_selection.hold_shift_to_delete");
        var activeTooltipComponent = Component.translatable("gui.waystones.waystone_selection.click_to_delete");
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL || WaystoneKinds.isSharestone(waystone.getWaystoneKind())) {
            final var warning = Component.translatable("gui.waystones.waystone_selection.deleting_global_for_all")
                    .withStyle(ChatFormatting.DARK_RED);
            tooltipComponent = tooltipComponent.copy().append("\n").append(warning);
            activeTooltipComponent = activeTooltipComponent.copy().append("\n").append(warning);
        }
        tooltip = Tooltip.create(tooltipComponent);
        activeTooltip = Tooltip.create(activeTooltipComponent);
        setTooltip(tooltip);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            shiftGuard = true;
            return true;
        }

        return false;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        boolean shiftDown = Kuma.hasShiftDown();
        if (!shiftDown) {
            shiftGuard = false;
        }
        active = !shiftGuard && shiftDown;
        setTooltip(active ? activeTooltip : tooltip);

        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, getX(), getY(), 13, 13, isHovered && active ? 0xFFFFFFFF : 0x80808080);
        }
    }
}
