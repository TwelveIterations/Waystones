package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.client.gui.screen.ManageWaystoneGroupsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ManageWaystoneGroupsList extends ContainerObjectSelectionList<ManageWaystoneGroupsList.GroupEntry> {

    private static final int MARGIN = 2;
    private static final int EDIT_BUTTON_WIDTH = 18;
    private static final int REMOVE_BUTTON_WIDTH = 18;
    private static final int BUTTON_RIGHT_OFFSET = EDIT_BUTTON_WIDTH + MARGIN + REMOVE_BUTTON_WIDTH + MARGIN;
    private static final int BUTTON_WIDTH = AbstractWaystoneList.ENTRY_WIDTH - BUTTON_RIGHT_OFFSET;

    private final ManageWaystoneGroupsScreen screen;

    public ManageWaystoneGroupsList(int x, int y, int width, int height, ManageWaystoneGroupsScreen screen) {
        super(Minecraft.getInstance(), width, height, y, AbstractWaystoneList.ENTRY_HEIGHT);
        this.screen = screen;
        setX(x);
    }

    public void setGroups(List<WaystoneGroup> groups) {
        clearEntries();
        for (WaystoneGroup group : groups) {
            addEntry(new GroupEntry(group));
        }
        refreshScrollAmount();
    }

    @Override
    public int getRowWidth() {
        return AbstractWaystoneList.ENTRY_WIDTH;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
    }

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor graphics) {
    }

    public class GroupEntry extends ContainerObjectSelectionList.Entry<GroupEntry> {

        private final ManageWaystoneGroupButton groupButton;
        private final EditWaystoneButton editButton;
        private final AbstractWidget actionButton;
        private final List<AbstractWidget> widgets;

        public GroupEntry(WaystoneGroup group) {
            groupButton = new ManageWaystoneGroupButton(BUTTON_WIDTH, group);
            widgets = new ArrayList<>();
            widgets.add(groupButton);

            editButton = new EditWaystoneButton(0,
                    0,
                    ManageWaystoneGroupsList.this.getY(),
                    ManageWaystoneGroupsList.this.getHeight(),
                    Component.translatable("container.waystones.edit_group"),
                    _ -> screen.editGroup(group));

            widgets.add(editButton);
            actionButton = group.inbuilt()
                    ? new ToggleWaystoneGroupHiddenButton(0,
                            0,
                            ManageWaystoneGroupsList.this.getY(),
                            ManageWaystoneGroupsList.this.getHeight(),
                            group.hidden(),
                            _ -> screen.toggleGroupHidden(group))
                    : new RemoveWaystoneGroupButton(0,
                            0,
                            ManageWaystoneGroupsList.this.getY(),
                            ManageWaystoneGroupsList.this.getHeight(),
                            _ -> screen.deleteGroup(group));
            widgets.add(actionButton);
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            updateWidgetPositions();
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            updateWidgetPositions();
        }

        private void updateWidgetPositions() {
            final int x = getX();
            final int y = getY() + 1;
            groupButton.setPosition(x, y);
            editButton.setPosition(x + BUTTON_WIDTH + MARGIN, y + 4);
            actionButton.setPosition(x + BUTTON_WIDTH + MARGIN + EDIT_BUTTON_WIDTH + MARGIN, y + 4);
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
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            widgets.forEach(widget -> widget.extractRenderState(graphics, mouseX, mouseY, partialTick));
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            widgets.forEach(consumer);
        }
    }
}
