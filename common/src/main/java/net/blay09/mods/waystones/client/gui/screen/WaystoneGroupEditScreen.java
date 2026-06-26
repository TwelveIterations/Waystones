package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.BalmEnvironment;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.client.gui.widget.ColorButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneGroupIconButton;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.ServerboundEditWaystoneGroupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class WaystoneGroupEditScreen extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private final ManageWaystoneGroupsScreen parent;
    private final Inventory playerInventory;
    private final Identifier groupId;
    private final String initialName;

    private @Nullable EditBox nameField;
    private @Nullable ColorButton colorButton;
    private @Nullable WaystoneGroupIconButton iconButton;

    public WaystoneGroupEditScreen(WaystoneSelectionMenu menu, Inventory playerInventory, ManageWaystoneGroupsScreen parent, Identifier groupId, String initialName) {
        super(menu, playerInventory, Component.translatable("container.waystones.edit_group"), 176, 120);
        this.parent = parent;
        this.playerInventory = playerInventory;
        this.groupId = groupId;
        this.initialName = initialName;
        titleLabelY = 20;
    }

    @Override
    public void init() {
        super.init();

        final var oldNameText = nameField != null ? nameField.getValue() : initialName;
        final var oldColor = colorButton != null ? colorButton.getColor() : getInitialColor();
        final var oldIcon = iconButton != null ? iconButton.getIcon() : getInitialIcon();
        var y = topPos + titleLabelY + 16;

        nameField = new EditBox(Minecraft.getInstance().font, leftPos, y + 1, 176, 19, nameField, Component.empty());
        nameField.setMaxLength(128);
        nameField.setValue(oldNameText);
        addRenderableWidget(nameField);
        setInitialFocus(nameField);
        y += 28;


        iconButton = new WaystoneGroupIconButton(leftPos, y, oldIcon);
        addRenderableWidget(iconButton);

        colorButton = new ColorButton(leftPos + 26, y, oldColor);
        addRenderableWidget(colorButton);
        y += 30;

        final var saveButton = Button.builder(
                        Component.translatable("gui.waystones.manage_groups.save"),
                        _ -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (nameField != null && nameField.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            nameField.setValue("");
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (nameField != null && (nameField.keyPressed(event) || nameField.isFocused())) {
            if (event.isEscape() || event.isConfirmation()) {
                this.onClose();
            }

            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);

        if (nameField != null && nameField.getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font,
                    Component.translatable("gui.waystones.manage_groups.unnamed_group"),
                    nameField.getX() + 4,
                    nameField.getY() + 6,
                    0xFF808080);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.centeredText(font, title, 176 / 2, titleLabelY, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        saveGroup();
        parent.returnFromEdit();
    }

    private void saveGroup() {
        final var name = nameField != null ? nameField.getValue() : initialName;
        final var groupName = name.trim().isEmpty()
                ? Component.translatable("gui.waystones.manage_groups.unnamed_group")
                : Component.literal(name);

        final var store = PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        final var groups = new ArrayList<>(store.getWaystoneGroupRegistry(playerInventory.player));
        final int existingGroupIndex = indexOfGroup(groups, groupId);
        final var existingGroup = existingGroupIndex != -1 ? groups.get(existingGroupIndex) : null;
        final var color = colorButton != null ? colorButton.getColor() : getInitialColor();
        final var icon = iconButton != null ? iconButton.getIcon() : getInitialIcon();
        final var group = existingGroup != null
                ? new WaystoneGroupImpl(groupId, groupName, icon, color, existingGroup.inbuilt(), existingGroup.hidden(), existingGroup.sortIndex())
                : new WaystoneGroupImpl(groupId, groupName, icon, color, false, false, groups.size());
        if (existingGroupIndex != -1) {
            groups.set(existingGroupIndex, group);
        } else {
            groups.add(group);
        }
        store.setWaystoneGroupRegistry(playerInventory.player, groups);
        Balm.networking().sendToServer(new ServerboundEditWaystoneGroupPacket(groupId, name, group.icon(), group.color(), group.hidden()));
    }

    private int getInitialColor() {
        final var existingGroup = findExistingGroup();
        return existingGroup != null ? existingGroup.color() : ColorButton.toArgb(DyeColor.WHITE);
    }

    private Identifier getInitialIcon() {
        final var existingGroup = findExistingGroup();
        return existingGroup != null ? existingGroup.icon() : WaystoneGroups.GLOBAL_ICON;
    }

    private @Nullable WaystoneGroup findExistingGroup() {
        final var store = PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        for (final var it : store.getWaystoneGroupRegistry(playerInventory.player)) {
            if (it.identifier().equals(groupId)) {
                return it;
            }
        }

        return null;
    }

    private static int indexOfGroup(ArrayList<WaystoneGroup> groups, Identifier groupId) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).identifier().equals(groupId)) {
                return i;
            }
        }

        return -1;
    }

}
