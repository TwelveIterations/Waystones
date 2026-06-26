package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.gui.screen.ManageWaystoneGroupsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageWaystoneGroupsList extends ContainerObjectSelectionList<ManageWaystoneGroupsList.GroupEntry> implements ListDragController {

    private static final int MARGIN = 2;
    private static final int DRAG_HANDLE_WIDTH = 16;
    private static final int EDIT_BUTTON_WIDTH = 18;
    private static final int REMOVE_BUTTON_WIDTH = 18;
    private static final int BUTTON_LEFT_OFFSET = DRAG_HANDLE_WIDTH + MARGIN;
    private static final int BUTTON_RIGHT_OFFSET = EDIT_BUTTON_WIDTH + MARGIN + REMOVE_BUTTON_WIDTH + MARGIN;
    private static final int BUTTON_WIDTH = AbstractWaystoneList.ENTRY_WIDTH - BUTTON_LEFT_OFFSET - BUTTON_RIGHT_OFFSET;

    private final ManageWaystoneGroupsScreen screen;
    private final List<GroupEntry> entries = new ArrayList<>();
    private @Nullable GroupEntry draggedEntry;
    private double dragMouseY;
    private double dragGrabOffsetY;
    private boolean extractingDraggedEntry;

    public ManageWaystoneGroupsList(int x, int y, int width, int height, ManageWaystoneGroupsScreen screen) {
        super(Minecraft.getInstance(), width, height, y, AbstractWaystoneList.ENTRY_HEIGHT);
        this.screen = screen;
        setX(x);
    }

    public void setGroups(List<WaystoneGroup> groups) {
        entries.clear();
        clearEntries();
        for (WaystoneGroup group : groups) {
            final var entry = new GroupEntry(group);
            entries.add(entry);
            addEntry(entry);
        }
        setScrollAmount(Math.min(getScrollAmount(), Math.max(0, getMaxPosition() - height)));
    }

    @Override
    public void startDragging(Object entry, double mouseY) {
        if (!(entry instanceof GroupEntry groupEntry)) {
            return;
        }
        draggedEntry = groupEntry;
        dragMouseY = mouseY;
        dragGrabOffsetY = mouseY - groupEntry.getY();
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

        int draggedIndex = entries.indexOf(draggedEntry);
        final int targetIndex = entries.indexOf(targetEntry);
        if (draggedIndex == -1 || targetIndex == -1) {
            return;
        }

        final int direction = Integer.compare(targetIndex, draggedIndex);
        while (draggedIndex != targetIndex) {
            final int nextIndex = draggedIndex + direction;
            final var nextEntry = entries.get(nextIndex);
            screen.reorderGroup(draggedEntry.group, nextEntry.group);
            Collections.swap(entries, draggedIndex, nextIndex);
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
        if (!(entry instanceof GroupEntry groupEntry)) {
            return;
        }
        final int currentIndex = entries.indexOf(groupEntry);
        final int targetIndex = 0;
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return;
        }

        screen.moveGroupToTop(groupEntry.group);

        int index = currentIndex;
        while (index != targetIndex) {
            Collections.swap(entries, index, index - 1);
            Collections.swap(children(), index, index - 1);
            index--;
        }
    }

    @Override
    public void moveToBottom(Object entry) {
        if (!(entry instanceof GroupEntry groupEntry)) {
            return;
        }
        final int currentIndex = entries.indexOf(groupEntry);
        final int targetIndex = entries.size() - 1;
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return;
        }

        screen.moveGroupToBottom(groupEntry.group);

        int index = currentIndex;
        while (index != targetIndex) {
            Collections.swap(entries, index, index + 1);
            Collections.swap(children(), index, index + 1);
            index++;
        }
    }

    @Override
    public boolean isDragging(Object entry) {
        return draggedEntry == entry;
    }

    @Override
    public int getRowWidth() {
        return AbstractWaystoneList.ENTRY_WIDTH;
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics graphics) {
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
                    AbstractWaystoneList.ENTRY_HEIGHT,
                    mouseX,
                    mouseY,
                    true,
                    partialTick);
            extractingDraggedEntry = false;
        }
    }

    public class GroupEntry extends ContainerObjectSelectionList.Entry<GroupEntry> {

        private final WaystoneGroup group;
        private final DragHandleButton dragHandleButton;
        private final ManageWaystoneGroupButton groupButton;
        private final EditWaystoneButton editButton;
        private final AbstractWidget actionButton;
        private final List<AbstractWidget> widgets = new ArrayList<>();
        private int x;
        private int y;

        public GroupEntry(WaystoneGroup group) {
            this.group = group;
            dragHandleButton = new DragHandleButton(ManageWaystoneGroupsList.this, this);
            widgets.add(dragHandleButton);

            groupButton = new ManageWaystoneGroupButton(BUTTON_WIDTH, group);
            widgets.add(groupButton);

            editButton = new EditWaystoneButton(0,
                    0,
                    ManageWaystoneGroupsList.this.getY(),
                    ManageWaystoneGroupsList.this.getHeight(),
                    Component.translatable("container.waystones.edit_group"),
                    button -> screen.editGroup(group));
            widgets.add(editButton);

            actionButton = group.inbuilt()
                    ? new ToggleWaystoneGroupHiddenButton(0,
                            0,
                            ManageWaystoneGroupsList.this.getY(),
                            ManageWaystoneGroupsList.this.getHeight(),
                            group.hidden(),
                            button -> screen.toggleGroupHidden(group))
                    : new RemoveWaystoneGroupButton(0,
                            0,
                            ManageWaystoneGroupsList.this.getY(),
                            ManageWaystoneGroupsList.this.getHeight(),
                            button -> screen.deleteGroup(group));
            widgets.add(actionButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
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
            x = left;
            y = top;

            if (ManageWaystoneGroupsList.this.isDragging(this)) {
                if (!extractingDraggedEntry) {
                    return;
                }

                final var pose = graphics.pose();
                pose.pushPose();
                pose.translate(0, (float) (dragMouseY - dragGrabOffsetY - getY()), 0);
                updateWidgetPositions();
                for (AbstractWidget widget : widgets) {
                    widget.render(graphics, mouseX, mouseY, partialTick);
                }
                pose.popPose();
                return;
            }

            updateWidgetPositions();
            for (AbstractWidget widget : widgets) {
                widget.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        private void updateWidgetPositions() {
            final int x = getX();
            final int y = getY() + 1;
            dragHandleButton.setPosition(x, y);
            groupButton.setPosition(x + BUTTON_LEFT_OFFSET, y);
            editButton.setPosition(x + BUTTON_LEFT_OFFSET + BUTTON_WIDTH + MARGIN, y + 4);
            actionButton.setPosition(x + BUTTON_LEFT_OFFSET + BUTTON_WIDTH + MARGIN + EDIT_BUTTON_WIDTH + MARGIN, y + 4);
        }
    }
}
