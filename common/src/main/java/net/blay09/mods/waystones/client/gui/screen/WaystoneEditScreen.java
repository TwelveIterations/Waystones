package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.network.message.ServerboundEditWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestManageWaystoneModifiersPacket;
import net.blay09.mods.waystones.network.message.ServerboundUserDecorateWaystonePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class WaystoneEditScreen extends AbstractContainerScreen<WaystoneEditMenu> {

    private @Nullable EditBox nameField;
    private @Nullable EditBox aliasField;
    private @Nullable WaystoneVisbilityButton visibilityButton;
    private @Nullable ImageButton modifierButton;
    private boolean aliasMode;

    public WaystoneEditScreen(WaystoneEditMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 210);
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();
        final var waystone = menu.getWaystone();
        final var oldNameText = nameField != null ? nameField.getValue() : waystone.getName().getString();
        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";
        final var oldAliasText = aliasField != null ? aliasField.getValue() : currentAlias;
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

        aliasField = new EditBox(Minecraft.getInstance().font, leftPos, y + 1, textFieldWidth, 19, aliasField, Component.empty());
        aliasField.setMaxLength(128);
        aliasField.setValue(oldAliasText);
        aliasField.setEditable(true);

        final var textField = getActiveTextField();
        addRenderableWidget(textField);
        if ((menu.canEdit() || aliasMode) && textField.getValue().isEmpty()) {
            setInitialFocus(textField);
        }
        final var aliasButtonLabel = Component.translatable(aliasMode
                ? "gui.waystones.waystone_settings.save_alias"
                : "gui.waystones.waystone_settings.configure_alias");
        final var aliasButtonSprites = aliasMode
                ? new WidgetSprites(
                        Identifier.withDefaultNamespace("waystones/save_button"),
                        Identifier.withDefaultNamespace("waystones/save_button_highlighted"))
                : new WidgetSprites(
                        Identifier.withDefaultNamespace("waystones/alias_button"),
                        Identifier.withDefaultNamespace("waystones/alias_button_highlighted"));
        final var aliasButton = new ImageButton(21,
                21,
                aliasButtonSprites,
                _ -> {
                    aliasMode = !aliasMode;
                    clearWidgets();
                    init();
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
                Identifier.withDefaultNamespace("waystones/modifier_button"),
                Identifier.withDefaultNamespace("waystones/modifier_button_highlighted"));
        modifierButton = new ImageButton(21,
                21,
                modifierSprites,
                (_) -> {
                    final var nameField = this.nameField;
                    Balm.networking()
                            .sendToServer(new ServerboundEditWaystonePacket(
                                    menu.getWaystone().getWaystoneUid(),
                                    nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString(),
                                    visibilityButton.getVisibility()));
                    Balm.networking().sendToServer(new ServerboundRequestManageWaystoneModifiersPacket(menu.getWaystone().getPos()));
                },
                Component.literal("gui.waystones.waystone_settings.manage_modifiers"));
        modifierButton.setPosition(leftPos, y);
        modifierButton.active = menu.canEdit();
        addRenderableWidget(modifierButton);
        y += 25;

        final var saveButton = Button.builder(
                        Component.translatable(menu.canEdit() || aliasMode ? "gui.waystones.waystone_settings.save" : "gui.waystones.waystone_settings.close"),
                        _ -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        final var textField = getActiveTextField();
        if (textField != null && (menu.canEdit() || aliasMode) && textField.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            textField.setValue("");
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        final var textField = getActiveTextField();
        if (textField != null && (textField.keyPressed(event) || textField.isFocused())) {
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

        final var textField = getActiveTextField();
        if (textField != null && (menu.canEdit() || aliasMode) && textField.getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font,
                    Component.translatable(aliasMode ? "gui.waystones.waystone_settings.no_alias" : "waystones.untitled_waystone"),
                    textField.getX() + 4,
                    textField.getY() + 6,
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
        if (visibilityButton != null) {
            final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";

            if (menu.canEdit()) {
                Balm.networking()
                        .sendToServer(new ServerboundEditWaystonePacket(
                                menu.getWaystone().getWaystoneUid(),
                                nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString(),
                                visibilityButton.getVisibility()));
            }

            if (aliasMode || aliasField != null && !aliasField.getValue().equals(currentAlias)) {
                Balm.networking()
                        .sendToServer(new ServerboundUserDecorateWaystonePacket(
                                menu.getWaystone().getWaystoneUid(),
                                aliasField != null ? aliasField.getValue() : ""));
            }
        }

        super.onClose();
    }

    private @Nullable EditBox getActiveTextField() {
        return aliasMode ? aliasField : nameField;
    }

}
