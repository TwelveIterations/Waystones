package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public class DragHandleButton extends Button {

    private static final Identifier ICON = id("widgets/drag_handle");
    private static final Identifier HIGHLIGHTED_ICON = id("widgets/drag_handle_highlighted");

    private static final Component NARRATE_INACTIVE = Component.translatable("gui.waystones.manage_waystones.narration.reorder");
    private static final Component NARRATE_ACTIVE = Component.translatable("gui.waystones.manage_waystones.narration.reorder.active");

    private boolean keyboardDragActive;
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
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_SPACE || event.key() == InputConstants.KEY_RETURN) {
            keyboardDragActive = !keyboardDragActive;
            return true;
        }

        if (!keyboardDragActive) {
            return super.keyPressed(event);
        }

        return switch (event.key()) {
            case InputConstants.KEY_UP -> {
                list.moveUp(entry);
                yield true;
            }
            case InputConstants.KEY_DOWN -> {
                list.moveDown(entry);
                yield true;
            }
            case InputConstants.KEY_HOME -> {
                list.moveToTop(entry);
                yield true;
            }
            case InputConstants.KEY_END -> {
                list.moveToBottom(entry);
                yield true;
            }
            default -> super.keyPressed(event);
        };
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        list.stopDragging();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            keyboardDragActive = false;
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        super.updateWidgetNarration(output);
        output.add(NarratedElementType.USAGE, keyboardDragActive ? NARRATE_ACTIVE : NARRATE_INACTIVE);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics) {
        final var icon = list.isDragging(entry) || keyboardDragActive || isHighlighted() ? HIGHLIGHTED_ICON : ICON;
        graphics.blitSprite(GuiPipeline.TEXTURED, icon, getX(), getY() + 2, 16, 16);
    }
}
