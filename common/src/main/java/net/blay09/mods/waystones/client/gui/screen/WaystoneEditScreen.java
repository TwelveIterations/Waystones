package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.network.message.ServerboundEditWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestManageWaystoneModifiersPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneEditScreen extends WaystoneContainerScreen<WaystoneEditMenu> {

    private final Inventory playerInventory;
    private @Nullable EditBox nameField;
    private @Nullable WaystoneVisbilityButton visibilityButton;
    private @Nullable ImageButton modifierButton;

    public WaystoneEditScreen(WaystoneEditMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 210);
        this.playerInventory = playerInventory;
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();
        final var waystone = menu.getWaystone();
        final var oldNameText = nameField != null ? nameField.getValue() : waystone.getName().getString();
        var oldVisibility = waystone.getVisibility();
        if (visibilityButton != null) {
            oldVisibility = visibilityButton.getVisibility();
        }

        var y = topPos + titleLabelY + 16;

        final var error = menu.getError();
        if (error != null) {
            y += 9;
        }

        final var textFieldWidth = 150;
        nameField = new EditBox(Minecraft.getInstance().font, leftPos, y + 1, textFieldWidth, 19, nameField, Component.empty());
        nameField.setMaxLength(128);
        nameField.setValue(oldNameText);
        nameField.setEditable(menu.canEdit());

        addRenderableWidget(nameField);
        if (menu.canEdit() && nameField.getValue().isEmpty()) {
            setInitialFocus(nameField);
        }
        final var aliasButtonLabel = Component.translatable("gui.waystones.waystone_settings.personal_settings");
        final var aliasButtonSprites = new WidgetSprites(
                id("widgets/alias_button"),
                id("widgets/alias_button_highlighted"));
        final var aliasButton = new ImageButton(21,
                21,
                aliasButtonSprites,
                _ -> {
                    saveWaystoneSettings();
                    final var personalizedWaystone = PlayerWaystoneManager.getPlayerDecoratedWaystone(playerInventory.player, menu.getWaystone());
                    openSiblingScreen(new PersonalWaystoneSettingsScreen(menu, playerInventory, personalizedWaystone, this));
                },
                aliasButtonLabel);
        aliasButton.setPosition(leftPos + 155, y);
        aliasButton.setTooltip(Tooltip.create(aliasButtonLabel));
        addRenderableWidget(aliasButton);
        y += 28;

        visibilityButton = new WaystoneVisbilityButton(leftPos, y, oldVisibility, menu.getVisibilityOptions(), menu.canEdit());
        visibilityButton.active = menu.canEdit() && menu.getVisibilityOptions().size() > 1;
        addRenderableWidget(visibilityButton);
        y += 25;

        final var modifierSprites = new WidgetSprites(
                id("edit_waystone/modifier_button"),
                id("edit_waystone/modifier_button_highlighted"));
        modifierButton = new ImageButton(21,
                21,
                modifierSprites,
                (_) -> {
                    saveWaystoneSettings();
                    Balm.networking().sendToServer(new ServerboundRequestManageWaystoneModifiersPacket(menu.getWaystone().getPos()));
                },
                Component.translatable("gui.waystones.waystone_settings.manage_modifiers"));
        modifierButton.setPosition(leftPos, y);
        modifierButton.active = menu.canEdit();
        addRenderableWidget(modifierButton);
        y += 25;

        final var saveButton = Button.builder(
                        Component.translatable(menu.canEdit() ? "gui.waystones.waystone_settings.save" : "gui.waystones.waystone_settings.close"),
                        _ -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (nameField != null && menu.canEdit() && nameField.isMouseOver(event.x(), event.y()) && event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
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

        if (nameField != null && menu.canEdit() && nameField.getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font,
                    Component.translatable("waystones.untitled_waystone"),
                    nameField.getX() + 4,
                    nameField.getY() + 6,
                    0xFF808080);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.centeredText(font, title, 176 / 2, titleLabelY, 0xFFFFFFFF);
        final var error = menu.getError();
        if (error != null) {
            guiGraphics.centeredText(font, error, 176 / 2, titleLabelY + 12, 0xFFFF5555);
        }
        if (visibilityButton != null) {
            guiGraphics.text(font,
                    Component.translatable("gui.waystones.waystone_settings.visibility." + visibilityButton.getVisibility().name().toLowerCase(Locale.ROOT)),
                    25,
                    visibilityButton.getY() - topPos + 6,
                    0xFFFFFFFF,
                    true);
        }
        if (modifierButton != null) {
            final var modifiersComponent = menu.getModifierCount() > 0
                    ? Component.translatable("gui.waystones.waystone_settings.modifiers_active", menu.getModifierCount())
                    : Component.translatable(
                    "gui.waystones.waystone_settings.no_modifiers_active");
            guiGraphics.text(font,
                    modifiersComponent,
                    25,
                    modifierButton.getY() - topPos + 6,
                    menu.getModifierCount() > 0 ? 0xFF55FF55 : 0xFFAAAAAA,
                    true);
        }
    }

    @Override
    public void onClose() {
        saveWaystoneSettings();
        super.onClose();
    }

    private void saveWaystoneSettings() {
        if (menu.canEdit()) {
            Balm.networking()
                    .sendToServer(new ServerboundEditWaystonePacket(
                            menu.getWaystone().getWaystoneUid(),
                            nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString(),
                            visibilityButton != null ? visibilityButton.getVisibility() : menu.getWaystone().getVisibility()));
        }
    }

}
