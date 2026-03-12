package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RemoveWaystoneButton extends Button implements ITooltipProvider {

    private static final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");

    private final List<Component> tooltip;
    private final List<Component> activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, Waystone waystone, OnPress pressable) {
        super(x, y, 18, 18, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        tooltip = Lists.newArrayList(Component.translatable("gui.waystones.waystone_selection.hold_shift_to_delete"));
        activeTooltip = Lists.newArrayList(Component.translatable("gui.waystones.waystone_selection.click_to_delete"));
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL || WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            var component = Component.translatable("gui.waystones.waystone_selection.deleting_global_for_all");
            component.withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
            activeTooltip.add(component);
        }
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

        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, CANCEL_SPRITE, getX(), getY(), 13, 13, isHovered && active ? 0xFFFFFFFF : 0x80808080);
        }
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHovered;
    }

    @Override
    public List<Component> getTooltipComponents() {
        return active ? activeTooltip : tooltip;
    }
}
