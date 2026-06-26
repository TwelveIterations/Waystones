package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class DragHandleButton extends Button {

    private static final ResourceLocation ICON = id("widgets/drag_handle");
    private static final ResourceLocation HIGHLIGHTED_ICON = id("widgets/drag_handle_highlighted");

    private final ListDragController list;
    private final Object entry;

    public DragHandleButton(ListDragController list, Object entry) {
        super(0, 0, 16, 20, Component.translatable("gui.waystones.manage_waystones.drag_to_reorder"), button -> {
        }, Button.DEFAULT_NARRATION);
        this.list = list;
        this.entry = entry;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        list.startDragging(entry, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        list.dragTo(mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        list.stopDragging();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        final var icon = list.isDragging(entry) || isHovered ? HIGHLIGHTED_ICON : ICON;
        graphics.blitSprite(icon, getX(), getY() + 2, 16, 16);
    }
}
