package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.MutablePersonalizedWaystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.api.WaystoneGroups;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.client.gui.widget.ManageWaystoneGroupsButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneGroupButton;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.ServerboundPersonalWaystoneSettingsPacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestEditWaystonePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public class PersonalWaystoneSettingsScreen extends WaystoneContainerScreen<AbstractContainerMenu> {

    private static final int MARGIN = 2;
    private static final int MANAGE_GROUPS_BUTTON_WIDTH = 20;

    private final MutablePersonalizedWaystone waystone;
    private final Inventory playerInventory;
    private final Screen parent;
    private @Nullable EditBox aliasField;
    private @Nullable WaystoneGroupButton groupButton;
    private @Nullable Checkbox favoriteCheckbox;
    private List<WaystoneGroup> groups = List.of();
    private @Nullable WaystoneGroup selectedGroup;

    public PersonalWaystoneSettingsScreen(AbstractContainerMenu menu, Inventory playerInventory, MutablePersonalizedWaystone waystone, Screen parent) {
        super(menu, playerInventory, Component.translatable("container.waystones.personal_waystone_settings", waystone.getName()), 176, 210);
        this.waystone = waystone;
        this.playerInventory = playerInventory;
        this.parent = parent;
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();

        final var currentAlias = waystone.getAlias().map(Component::getString).orElse("");
        final var oldAliasText = aliasField != null ? aliasField.getValue() : currentAlias;
        final var oldSelectedGroup = selectedGroup;
        final var oldFavoriteSelected = favoriteCheckbox != null ? favoriteCheckbox.selected() : isFavoriteConfigured();
        final var y = topPos + titleLabelY + 16;
        final var canEditWaystone = canEditWaystone();
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
                    _ -> {
                        savePersonalWaystoneSettings();
                        Balm.networking().sendToServer(new ServerboundRequestEditWaystonePacket(waystone.getWaystoneUid()));
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
                _ -> cycleGroup(1),
                _ -> cycleGroup(-1));
        addRenderableWidget(groupButton);

        final var manageGroupsButton = new ManageWaystoneGroupsButton(leftPos + groupButton.getWidth() + MARGIN,
                y + 26,
                _ -> openManageGroupsScreen());
        addRenderableWidget(manageGroupsButton);

        favoriteCheckbox = Checkbox.builder(Component.translatable("gui.waystones.personal_waystone_settings.favorite"), font)
                .pos(leftPos, y + 52)
                .selected(oldFavoriteSelected)
                .maxWidth(176)
                .build();
        addRenderableWidget(favoriteCheckbox);

        final var saveButton = Button.builder(
                        Component.translatable("gui.waystones.personal_waystone_settings.save"),
                        _ -> onClose())
                .pos(leftPos + 176 / 2 - 50, y + 78)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (aliasField != null && aliasField.isMouseOver(event.x(), event.y()) && event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            aliasField.setValue("");
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (aliasField != null && (aliasField.keyPressed(event) || aliasField.isFocused())) {
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

        if (aliasField != null && aliasField.getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font,
                    Component.translatable("gui.waystones.personal_waystone_settings.no_alias"),
                    aliasField.getX() + 4,
                    aliasField.getY() + 6,
                    0xFF808080);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.centeredText(font, title, 176 / 2, titleLabelY, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        savePersonalWaystoneSettings();
        openSiblingScreen(parent);
    }

    private void savePersonalWaystoneSettings() {
        final var currentAlias = waystone.getAlias().map(Component::getString).orElse("");
        final var selectedGroupIds = getSelectedGroupIds();
        if (aliasField != null && (!aliasField.getValue().equals(currentAlias) || !selectedGroupIds.equals(waystone.getConfiguredGroups()))) {
            final Optional<Component> alias = aliasField.getValue().trim().isEmpty()
                    ? Optional.empty()
                    : Optional.of(Component.literal(aliasField.getValue()));
            waystone.setAlias(alias.orElse(null));
            waystone.setConfiguredGroups(selectedGroupIds);
            PlayerWaystoneManager.setWaystoneAlias(playerInventory.player, waystone.getWaystoneUid(), alias.orElse(null));
            PlayerWaystoneManager.setConfiguredWaystoneGroups(playerInventory.player, waystone.getWaystoneUid(), selectedGroupIds);
            Balm.networking()
                    .sendToServer(new ServerboundPersonalWaystoneSettingsPacket(
                            waystone.getWaystoneUid(),
                            alias,
                            selectedGroupIds,
                            waystone.isHidden()));
        }
    }

    private Set<Identifier> getSelectedGroupIds() {
        final var selectedGroupIds = new LinkedHashSet<Identifier>();
        if (selectedGroup != null) {
            selectedGroupIds.add(selectedGroup.identifier());
        }
        if (favoriteCheckbox != null && favoriteCheckbox.selected()) {
            selectedGroupIds.add(WaystoneGroups.FAVORITES.identifier());
        }
        return Set.copyOf(selectedGroupIds);
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
        openSiblingScreen(new ManageWaystoneGroupsScreen(menu, playerInventory, this));
    }

    private @Nullable WaystoneGroup getStillAvailableGroup(@Nullable WaystoneGroup group) {
        return group != null && WaystoneGroups.indexOfGroup(groups, group) != -1 ? group : null;
    }

    private @Nullable WaystoneGroup getSelectedGroup() {
        final var configuredGroups = waystone.getConfiguredGroups();
        return groups.stream()
                .filter(group -> configuredGroups.contains(group.identifier()))
                .findFirst()
                .orElse(null);
    }

    private boolean isFavoriteConfigured() {
        return waystone.getConfiguredGroups().contains(WaystoneGroups.FAVORITES.identifier());
    }

    private boolean canEditWaystone() {
        return !waystone.getWaystoneKind().equals(WaystoneKinds.TWINBOUND_FEATHER) && isWaystoneInRange();
    }

    private boolean isWaystoneInRange() {
        final var player = Minecraft.getInstance().player;
        if (player == null || !player.level().dimension().equals(waystone.getDimension())) {
            return false;
        }

        final var pos = waystone.getPos();
        return player.distanceToSqr(Vec3.atCenterOf(pos)) <= 64;
    }
}
