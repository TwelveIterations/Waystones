package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.network.message.ServerboundEditWaystonePacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestManageWaystoneModifiersPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class WaystoneEditScreen extends AbstractContainerScreen<WaystoneEditMenu> {

    private EditBox textField;
    private WaystoneVisbilityButton visibilityButton;
    private ImageButton modifierButton;
    private Button saveButton;

    public WaystoneEditScreen(WaystoneEditMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 210);
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();
        final var waystone = menu.getWaystone();
        var oldText = waystone.getName().getString();
        if (textField != null) {
            oldText = textField.getValue();
        }
        var oldVisibility = waystone.getVisibility();
        if (visibilityButton != null) {
            oldVisibility = visibilityButton.getVisibility();
        }

        var y = topPos + titleLabelY + 16;

        final var error = menu.getError();
        if (error != null) {
            y += 9;
        }

        textField = new EditBox(Minecraft.getInstance().font, leftPos, y, 176, 20, textField, Component.empty());
        textField.setMaxLength(128);
        textField.setValue(oldText);
        textField.setEditable(menu.canEdit());
        addRenderableWidget(textField);
        if (menu.canEdit() && oldText.isEmpty()) {
            setInitialFocus(textField);
        }
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
                    Balm.networking()
                            .sendToServer(new ServerboundEditWaystonePacket(menu.getWaystone().getWaystoneUid(), textField.getValue(), visibilityButton.getVisibility()));
                    Balm.networking().sendToServer(new ServerboundRequestManageWaystoneModifiersPacket(menu.getWaystone().getPos()));
                },
                Component.literal("gui.waystones.waystone_settings.manage_modifiers"));
        modifierButton.setPosition(leftPos, y);
        addRenderableWidget(modifierButton);
        y += 25;

        saveButton = Button.builder(menu.canEdit() ? Component.translatable("gui.waystones.waystone_settings.save") : Component.translatable(
                        "gui.waystones.waystone_settings.close"), it -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (menu.canEdit() && textField.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            textField.setValue("");
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (textField.keyPressed(event) || textField.isFocused()) {
            if (event.isEscape() || event.isConfirmation()) {
                this.onClose();
            }

            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        extractTooltip(guiGraphics, mouseX, mouseY);

        if (textField != null && textField.getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font,
                    Component.translatable("waystones.untitled_waystone"),
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
            guiGraphics.centeredText(font, error, 176 / 2, titleLabelY + 12, ChatFormatting.RED.getColor());
        }
        guiGraphics.text(font,
                Component.translatable("gui.waystones.waystone_settings.visibility." + visibilityButton.getVisibility().name().toLowerCase(Locale.ROOT)),
                25,
                visibilityButton.getY() - topPos + 6,
                0xFFFFFFFF,
                true);
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

    @Override
    public void onClose() {
        if (textField != null && visibilityButton != null) {
            Balm.networking()
                    .sendToServer(new ServerboundEditWaystonePacket(menu.getWaystone().getWaystoneUid(), textField.getValue(), visibilityButton.getVisibility()));
        }

        super.onClose();
    }

}
