package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystoneGroupsButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneGroupButton;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.PersonalWaystoneSettingsMenu;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.blay09.mods.waystones.network.message.UserDecorateWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public class PersonalWaystoneSettingsScreen extends AbstractContainerScreen<PersonalWaystoneSettingsMenu> {

    private static final int MARGIN = 2;
    private static final int MANAGE_GROUPS_BUTTON_WIDTH = 20;

    private final Inventory playerInventory;
    private @Nullable EditBox aliasField;
    private @Nullable WaystoneGroupButton groupButton;
    private List<WaystoneGroup> groups = List.of();
    private @Nullable WaystoneGroup selectedGroup;

    public PersonalWaystoneSettingsScreen(PersonalWaystoneSettingsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 210;
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();

        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";
        final var oldAliasText = aliasField != null ? aliasField.getValue() : currentAlias;
        final var oldSelectedGroup = selectedGroup;
        final var y = topPos + titleLabelY + 16;
        final var canEditWaystone = isWaystoneInRange();
        final var aliasFieldWidth = canEditWaystone ? 150 : 176;
        groups = WaystoneGroups.sorted(PlayerWaystoneManager.getWaystoneGroupRegistry(minecraft.player))
                .stream()
                .filter(group -> !group.inbuilt() && !group.hidden())
                .toList();
        selectedGroup = groupButton != null
                ? getStillAvailableGroup(oldSelectedGroup)
                : getSelectedGroup();

        aliasField = new EditBox(Minecraft.getInstance().font, leftPos, y + 1, aliasFieldWidth, 19, aliasField, Component.empty());
        aliasField.setMaxLength(128);
        aliasField.setValue(oldAliasText);
        addRenderableWidget(aliasField);
        if (aliasField.getValue().isEmpty()) {
            setInitialFocus(aliasField);
        }

        if (canEditWaystone) {
            final var editButtonLabel = Component.translatable("gui.waystones.personal_waystone_settings.configure_waystone");
            final var editButtonSprites = new WidgetSprites(
                    id("widgets/edit_button"),
                    id("widgets/edit_button_highlighted"));
            final var editButton = new ImageButton(21,
                    21,
                    editButtonSprites,
                    button -> {
                        savePersonalWaystoneSettings();
                        Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(menu.getWaystone().getPos()));
                    },
                    editButtonLabel);
            editButton.setPosition(leftPos + 155, y);
            editButton.setTooltip(Tooltip.create(editButtonLabel));
            addRenderableWidget(editButton);
        }

        final var emptyGroupLabel = getEmptyGroupLabel();
        groupButton = new WaystoneGroupButton(leftPos,
                y + 26,
                172 - MARGIN - MANAGE_GROUPS_BUTTON_WIDTH,
                selectedGroup,
                emptyGroupLabel,
                button -> cycleGroup(1),
                button -> cycleGroup(-1));
        addRenderableWidget(groupButton);

        final var manageGroupsButton = new ManageWaystoneGroupsButton(leftPos + groupButton.getWidth() + MARGIN,
                y + 26,
                button -> openManageGroupsScreen());
        addRenderableWidget(manageGroupsButton);

        final var saveButton = Button.builder(
                        Component.translatable("gui.waystones.personal_waystone_settings.save"),
                        button -> onClose())
                .pos(leftPos + 176 / 2 - 50, y + 78)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (aliasField != null && aliasField.isMouseOver(mouseX, mouseY) && button == 1) {
            aliasField.setValue("");
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (aliasField != null && (aliasField.keyPressed(keyCode, scanCode, modifiers) || aliasField.isFocused())) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                this.onClose();
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        renderTooltip(guiGraphics, mouseX, mouseY);

        if (aliasField != null && aliasField.getValue().isEmpty()) {
            guiGraphics.drawString(Minecraft.getInstance().font,
                    Component.translatable("gui.waystones.personal_waystone_settings.no_alias"),
                    aliasField.getX() + 4,
                    aliasField.getY() + 6,
                    0x808080);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(font, title, 176 / 2, titleLabelY, 0xFFFFFFFF);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
    }

    @Override
    public void onClose() {
        savePersonalWaystoneSettings();
        super.onClose();
    }

    private void savePersonalWaystoneSettings() {
        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";
        final var selectedGroupIds = selectedGroup != null ? Set.of(selectedGroup.identifier()) : Set.<ResourceLocation>of();
        if (aliasField != null && (!aliasField.getValue().equals(currentAlias) || !selectedGroupIds.equals(menu.getConfiguredGroups()))) {
            Balm.getNetworking()
                    .sendToServer(new UserDecorateWaystoneMessage(
                            menu.getWaystone().getWaystoneUid(),
                            aliasField.getValue().trim().isEmpty() ? Optional.empty() : Optional.of(Component.literal(aliasField.getValue())),
                            Optional.ofNullable(selectedGroup).map(WaystoneGroup::identifier)));
        }
    }

    private void cycleGroup(int direction) {
        if (groups.isEmpty()) {
            selectedGroup = null;
            if (groupButton != null) {
                groupButton.setGroup(null, getEmptyGroupLabel());
            }
            return;
        }

        final var currentGroupId = selectedGroup != null ? selectedGroup.identifier() : null;
        final int currentIndex = currentGroupId != null ? WaystoneGroups.indexOfGroup(groups, currentGroupId) : -1;
        final int nextIndex;
        if (direction > 0) {
            nextIndex = currentIndex + 1 < groups.size() ? currentIndex + 1 : -1;
        } else {
            nextIndex = currentIndex == -1 ? groups.size() - 1 : currentIndex - 1;
        }
        selectedGroup = nextIndex == -1 ? null : groups.get(nextIndex);
        if (groupButton != null) {
            groupButton.setGroup(selectedGroup, getEmptyGroupLabel());
        }
    }

    private Component getEmptyGroupLabel() {
        return Component.translatable(groups.isEmpty()
                ? "gui.waystones.personal_waystone_settings.no_groups_defined"
                : "gui.waystones.personal_waystone_settings.no_group");
    }

    private void openManageGroupsScreen() {
        savePersonalWaystoneSettings();
        Minecraft.getInstance().setScreen(new ManageWaystoneGroupsScreen(menu, playerInventory, this));
    }

    private @Nullable WaystoneGroup getStillAvailableGroup(@Nullable WaystoneGroup group) {
        return group != null && WaystoneGroups.indexOfGroup(groups, group) != -1 ? group : null;
    }

    private @Nullable WaystoneGroup getSelectedGroup() {
        final var configuredGroups = menu.getConfiguredGroups();
        return groups.stream()
                .filter(group -> configuredGroups.contains(group.identifier()))
                .findFirst()
                .orElse(null);
    }

    private boolean isWaystoneInRange() {
        final var player = Minecraft.getInstance().player;
        if (player == null || !player.level().dimension().equals(menu.getWaystone().getDimension())) {
            return false;
        }

        final var pos = menu.getWaystone().getPos();
        return player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= 64;
    }
}
