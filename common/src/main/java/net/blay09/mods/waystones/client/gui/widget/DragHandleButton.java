package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class DragHandleButton extends Button {

    private static final Identifier ICON = id("waystone_selection/drag_handle");
    private static final Identifier HIGHLIGHTED_ICON = id("waystone_selection/drag_handle_highlighted");

    private final ListDragController list;
    private final Object entry;

    public DragHandleButton(ListDragController list, Object entry) {
        super(0, 0, 16, 20, Component.translatable("gui.waystones.manage_waystones.drag_to_reorder"), _ -> {
        }, Button.DEFAULT_NARRATION);
        this.list = list;
        this.entry = entry;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (doubleClick) {
            list.stopDragging();
            if (event.hasShiftDown()) {
                list.moveToBottom(entry);
            } else {
                list.moveToTop(entry);
            }
            return;
        }

        list.startDragging(entry, event.y());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        list.dragTo(event.y());
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        list.stopDragging();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        final var icon = list.isDragging(entry) || isHovered ? HIGHLIGHTED_ICON : ICON;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, getX(), getY() + 2, 16, 16);
    }
}
