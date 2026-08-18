package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.client.gui.screen.ManageWaystonesScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageWaystonesList extends AbstractWaystoneList<ManageWaystonesList.WaystoneEntry> implements ListDragController {

    private static final int MARGIN = 2;
    private static final int DRAG_HANDLE_WIDTH = 16;
    private static final int EDIT_BUTTON_WIDTH = 18;
    private static final int REMOVE_BUTTON_WIDTH = 18;
    private static final int BUTTON_LEFT_OFFSET = DRAG_HANDLE_WIDTH + MARGIN;
    private static final int BUTTON_RIGHT_OFFSET = EDIT_BUTTON_WIDTH + MARGIN + REMOVE_BUTTON_WIDTH + MARGIN;
    private static final int BUTTON_WIDTH = ENTRY_WIDTH - BUTTON_LEFT_OFFSET - BUTTON_RIGHT_OFFSET;

    private final ManageWaystonesScreen screen;
    private @Nullable WaystoneEntry draggedEntry;
    private double dragMouseY;
    private double dragGrabOffsetY;
    private boolean extractingDraggedEntry;

    public ManageWaystonesList(int x,
                               int y,
                               int width,
                               int height,
                               ManageWaystonesScreen screen) {
        super(x, y, width, height);
        this.screen = screen;
    }

    @Override
    protected WaystoneEntry createEntry(Waystone waystone, int index, int waystoneCount) {
        return new WaystoneEntry(waystone);
    }

    @Override
    public void startDragging(Object entry, double mouseY) {
        if (!(entry instanceof WaystoneEntry waystoneEntry)) {
            return;
        }
        draggedEntry = waystoneEntry;
        dragMouseY = mouseY;
        dragGrabOffsetY = mouseY - waystoneEntry.getY();
    }

    @Override
    public void dragTo(double mouseY) {
        if (draggedEntry == null) {
            return;
        }

        dragMouseY = mouseY;

        final int edgeScrollArea = 12;
        if (mouseY < getY() + edgeScrollArea) {
            setScrollAmount(getScrollAmount() - 4);
        } else if (mouseY > getBottom() - edgeScrollArea) {
            setScrollAmount(getScrollAmount() + 4);
        }

        final var targetEntry = getEntryAtPosition(getRowLeft(), mouseY);
        if (targetEntry == null || targetEntry == draggedEntry) {
            return;
        }

        int draggedIndex = children().indexOf(draggedEntry);
        final int targetIndex = children().indexOf(targetEntry);
        if (draggedIndex == -1 || targetIndex == -1) {
            return;
        }

        final int direction = Integer.compare(targetIndex, draggedIndex);
        while (draggedIndex != targetIndex) {
            final int nextIndex = draggedIndex + direction;
            final var nextEntry = children().get(nextIndex);
            screen.reorderWaystone(draggedEntry.waystone, nextEntry.waystone);
            Collections.swap(children(), draggedIndex, nextIndex);
            draggedIndex = nextIndex;
        }
    }

    @Override
    public void stopDragging() {
        draggedEntry = null;
    }

    @Override
    public void moveToTop(Object entry) {
        if (!(entry instanceof WaystoneEntry waystoneEntry)) {
            return;
        }
        final int currentIndex = children().indexOf(waystoneEntry);
        final int targetIndex = 0;
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return;
        }

        screen.moveWaystoneToTop(waystoneEntry.waystone);

        int index = currentIndex;
        while (index != targetIndex) {
            Collections.swap(children(), index, index - 1);
            index--;
        }
    }

    @Override
    public void moveToBottom(Object entry) {
        if (!(entry instanceof WaystoneEntry waystoneEntry)) {
            return;
        }
        final int currentIndex = children().indexOf(waystoneEntry);
        final int targetIndex = children().size() - 1;
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return;
        }

        screen.moveWaystoneToBottom(waystoneEntry.waystone);

        int index = currentIndex;
        while (index != targetIndex) {
            Collections.swap(children(), index, index + 1);
            index++;
        }
    }

    @Override
    public boolean isDragging(Object entry) {
        return draggedEntry == entry;
    }

    @Override
    protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        extractingDraggedEntry = false;
        super.renderListItems(graphics, mouseX, mouseY, partialTick);

        if (draggedEntry != null) {
            extractingDraggedEntry = true;
            draggedEntry.render(graphics,
                    children().indexOf(draggedEntry),
                    draggedEntry.getY(),
                    getRowLeft(),
                    getRowWidth(),
                    ENTRY_HEIGHT,
                    mouseX,
                    mouseY,
                    true,
                    partialTick);
            extractingDraggedEntry = false;
        }
    }

    public class WaystoneEntry extends Entry<WaystoneEntry> {

        private final Waystone waystone;
        private final List<AbstractWidget> widgets = new ArrayList<>();
        private final @Nullable ManageWaystoneButton waystoneButton;
        private final @Nullable DragHandleButton dragHandleButton;
        private final @Nullable EditWaystoneButton editButton;
        private final @Nullable RemoveWaystoneButton removeButton;
        private final @Nullable ToggleWaystoneHiddenButton hiddenButton;

        public WaystoneEntry(Waystone waystone) {
            this.waystone = waystone;

            if (screen.canReorderWaystones()) {
                dragHandleButton = new DragHandleButton(ManageWaystonesList.this, this);
                widgets.add(dragHandleButton);
            } else {
                dragHandleButton = null;
            }

            waystoneButton = new ManageWaystoneButton(BUTTON_WIDTH, waystone);
            widgets.add(waystoneButton);

            if (screen.canEditPersonalWaystoneSettings(waystone)) {
                editButton = new EditWaystoneButton(0,
                        0,
                        ManageWaystonesList.this.getY(),
                        ManageWaystonesList.this.getHeight(),
                        Component.translatable("gui.waystones.waystone_selection.edit_personal_settings"),
                        button -> screen.openPersonalWaystoneSettings(waystone));
                widgets.add(editButton);
            } else {
                editButton = null;
            }

            if (screen.canDeleteWaystone(waystone)) {
                removeButton = new RemoveWaystoneButton(0,
                        0,
                        ManageWaystonesList.this.getY(),
                        ManageWaystonesList.this.getHeight(),
                        waystone,
                        button -> screen.deleteWaystone(waystone));
                widgets.add(removeButton);
            } else {
                removeButton = null;
            }

            if (screen.canToggleWaystoneHidden(waystone)) {
                hiddenButton = new ToggleWaystoneHiddenButton(0,
                        0,
                        ManageWaystonesList.this.getY(),
                        ManageWaystonesList.this.getHeight(),
                        () -> screen.isWaystoneHidden(waystone),
                        button -> screen.toggleWaystoneHidden(waystone));
                widgets.add(hiddenButton);
            } else {
                hiddenButton = null;
            }
        }

        @Override
        public void render(GuiGraphics graphics,
                           int index,
                           int top,
                           int left,
                           int width,
                           int height,
                           int mouseX,
                           int mouseY,
                           boolean hovered,
                           float partialTick) {
            if (ManageWaystonesList.this.isDragging(this)) {
                if (!extractingDraggedEntry) {
                    return;
                }

                final var pose = graphics.pose();
                pose.pushPose();
                pose.translate(0, (float) (dragMouseY - dragGrabOffsetY - getY()), 0);
                super.render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTick);
                pose.popPose();
                return;
            }

            super.render(graphics, index, top, left, width, height, mouseX, mouseY, hovered, partialTick);
        }

        @Override
        protected void updateWidgetPositions() {
            if (waystoneButton == null) {
                return;
            }
            final int x = getX();
            final int y = getY() + 1;
            if (dragHandleButton != null) {
                dragHandleButton.setPosition(x, y);
            }
            waystoneButton.setPosition(x + BUTTON_LEFT_OFFSET, y);
            if (editButton != null) {
                editButton.setPosition(x + BUTTON_LEFT_OFFSET + BUTTON_WIDTH + MARGIN, y + 4);
            }
            if (removeButton != null) {
                removeButton.setPosition(x + BUTTON_LEFT_OFFSET + BUTTON_WIDTH + MARGIN + EDIT_BUTTON_WIDTH + MARGIN, y + 4);
            }
            if (hiddenButton != null) {
                hiddenButton.setPosition(x + BUTTON_LEFT_OFFSET + BUTTON_WIDTH + MARGIN + EDIT_BUTTON_WIDTH + MARGIN, y + 4);
            }
        }

        @Override
        protected List<AbstractWidget> widgets() {
            return widgets;
        }
    }
}
