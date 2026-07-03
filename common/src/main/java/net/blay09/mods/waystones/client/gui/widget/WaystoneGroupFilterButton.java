package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class WaystoneGroupFilterButton extends Button {

    private final WaystoneGroup group;
    private final BooleanSupplier selected;

    public WaystoneGroupFilterButton(int x, int y, WaystoneGroup group, BooleanSupplier selected, OnPress pressable) {
        super(x, y, 20, 20, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.group = group;
        this.selected = selected;
        setTooltip(Tooltip.create(group.name()));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        graphics.blitSprite(WaystoneGroupIcons.getIcon(group), getX() + 2, getY() + 2, 16, 16);
        if (selected.getAsBoolean()) {
            graphics.renderOutline(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 0xFFFFFFFF);
        }
    }
}
