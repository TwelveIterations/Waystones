package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class WaystoneGroupFilterButton extends Button.Plain {

    private final WaystoneGroup group;
    private final BooleanSupplier selected;

    public WaystoneGroupFilterButton(int x, int y, WaystoneGroup group, BooleanSupplier selected, OnPress pressable) {
        super(x, y, 20, 20, Component.empty(), pressable, _ -> group.name().copy());
        this.group = group;
        this.selected = selected;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WaystoneGroupIcons.getIcon(group), getX() + 2, getY() + 2, 16, 16);
        if (selected.getAsBoolean()) {
            graphics.outline(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 0xFFFFFFFF);
        }
        if (isHovered) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, group.name(), mouseX, mouseY, null);
        }
    }
}
