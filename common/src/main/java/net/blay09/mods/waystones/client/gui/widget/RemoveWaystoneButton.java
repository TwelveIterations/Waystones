package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RemoveWaystoneButton extends Button implements ITooltipProvider {

    private static final ResourceLocation BEACON = new ResourceLocation("textures/gui/container/beacon.png");

    private final List<Component> tooltip;
    private final List<Component> activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, IWaystone waystone, OnPress pressable) {
        super(x, y, 13, 13, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        tooltip = Lists.newArrayList(Component.translatable("gui.waystones.waystone_selection.hold_shift_to_delete"));
        activeTooltip = Lists.newArrayList(Component.translatable("gui.waystones.waystone_selection.click_to_delete"));
        if (waystone.isGlobal()) {
            var component = Component.translatable("gui.waystones.waystone_selection.deleting_global_for_all");
            component.withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
            activeTooltip.add(component);
        }
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

        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            if (isHovered && active) {
                guiGraphics.setColor(1f, 1f, 1f, 1f);
            } else {
                guiGraphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            guiGraphics.blit(BEACON, getX(), getY(), 114, 223, 13, 13);
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
