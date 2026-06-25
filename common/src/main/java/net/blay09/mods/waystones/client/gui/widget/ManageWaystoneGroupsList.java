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

import java.util.ArrayList;
import java.util.List;

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
        setScrollAmount(Math.min(getScrollAmount(), Math.max(0, getMaxPosition() - height)));
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

    public class GroupEntry extends ContainerObjectSelectionList.Entry<GroupEntry> {

        private final ManageWaystoneGroupButton groupButton;
        private final EditWaystoneButton editButton;
        private final AbstractWidget actionButton;
        private final List<AbstractWidget> widgets = new ArrayList<>();

        public GroupEntry(WaystoneGroup group) {
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
            final int y = top + 1;
            groupButton.setPosition(left, y);
            editButton.setPosition(left + BUTTON_WIDTH + MARGIN, y + 4);
            actionButton.setPosition(left + BUTTON_WIDTH + MARGIN + EDIT_BUTTON_WIDTH + MARGIN, y + 4);
            for (AbstractWidget widget : widgets) {
                widget.render(graphics, mouseX, mouseY, partialTick);
            }
        }
    }
}
