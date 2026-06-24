package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.blay09.mods.waystones.network.message.RequestManageWaystoneModifiersMessage;
import net.blay09.mods.waystones.network.message.UserDecorateWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class WaystoneEditScreen extends AbstractContainerScreen<WaystoneEditMenu> {

    private @Nullable EditBox nameField;
    private @Nullable EditBox aliasField;
    private @Nullable WaystoneVisbilityButton visibilityButton;
    private @Nullable ImageButton modifierButton;
    private boolean aliasMode;

    public WaystoneEditScreen(WaystoneEditMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageHeight = 210;
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
                ResourceLocation.withDefaultNamespace("waystones/save_button"),
                ResourceLocation.withDefaultNamespace("waystones/save_button_highlighted"))
                : new WidgetSprites(
                ResourceLocation.withDefaultNamespace("waystones/alias_button"),
                ResourceLocation.withDefaultNamespace("waystones/alias_button_highlighted"));
        final var aliasButton = new ImageButton(21,
                21,
                aliasButtonSprites,
                button -> {
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
        y += 24;

        final var modifierSprites = new WidgetSprites(
                ResourceLocation.withDefaultNamespace("waystones/modifier_button"),
                ResourceLocation.withDefaultNamespace("waystones/modifier_button_highlighted"));
        modifierButton = new ImageButton(20,
                20,
                modifierSprites,
                button -> {
                    final var currentName = nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString();
                    Balm.getNetworking().sendToServer(new EditWaystoneMessage(menu.getWaystone().getWaystoneUid(), currentName, visibilityButton.getVisibility()));
                    Balm.getNetworking().sendToServer(new RequestManageWaystoneModifiersMessage(menu.getWaystone().getPos()));
                },
                Component.literal("gui.waystones.waystone_settings.manage_modifiers"));
        modifierButton.setPosition(leftPos, y);
        modifierButton.active = menu.canEdit();
        addRenderableWidget(modifierButton);
        y += 24;

        final var saveButton = Button.builder(
                        Component.translatable(menu.canEdit() || aliasMode ? "gui.waystones.waystone_settings.save" : "gui.waystones.waystone_settings.close"),
                        button -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final var textField = getActiveTextField();
        if (textField != null && (menu.canEdit() || aliasMode) && textField.isMouseOver(mouseX, mouseY) && button == 1) {
            textField.setValue("");
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        final var textField = getActiveTextField();
        if (textField != null && (textField.keyPressed(keyCode, scanCode, modifiers) || textField.isFocused())) {
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

        final var textField = getActiveTextField();
        if (textField != null && (menu.canEdit() || aliasMode) && textField.getValue().isEmpty()) {
            guiGraphics.drawString(Minecraft.getInstance().font,
                    Component.translatable(aliasMode ? "gui.waystones.waystone_settings.no_alias" : "waystones.untitled_waystone"),
                    textField.getX() + 4,
                    textField.getY() + 6,
                    0x808080);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(font, title, 176 / 2, titleLabelY, 0xFFFFFFFF);
        final var error = menu.getError();
        if (error != null) {
            guiGraphics.drawCenteredString(font, error, 176 / 2, titleLabelY + 12, 0xFFFF5555);
        }
        guiGraphics.drawString(font,
                Component.translatable("gui.waystones.waystone_settings.visibility." + visibilityButton.getVisibility().name().toLowerCase(Locale.ROOT)),
                24,
                visibilityButton.getY() - topPos + 6,
                0xFFFFFFFF,
                true);
        final var modifiersComponent = menu.getModifierCount() > 0
                ? Component.translatable("gui.waystones.waystone_settings.modifiers_active", menu.getModifierCount())
                : Component.translatable("gui.waystones.waystone_settings.no_modifiers_active");
        guiGraphics.drawString(font,
                modifiersComponent,
                24,
                modifierButton.getY() - topPos + 6,
                menu.getModifierCount() > 0 ? 0xFf55Ff55 : 0xfFaAaAaA,
                true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
    }

    @Override
    public void onClose() {
        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";

        if (menu.canEdit()) {
            Balm.getNetworking()
                    .sendToServer(new EditWaystoneMessage(
                            menu.getWaystone().getWaystoneUid(),
                            nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString(),
                            visibilityButton != null ? visibilityButton.getVisibility() : menu.getWaystone().getVisibility()));
        }

        if (aliasMode || aliasField != null && !aliasField.getValue().equals(currentAlias)) {
            Balm.getNetworking()
                    .sendToServer(new UserDecorateWaystoneMessage(
                            menu.getWaystone().getWaystoneUid(),
                            aliasField != null ? aliasField.getValue() : ""));
        }

        super.onClose();
    }

    private @Nullable EditBox getActiveTextField() {
        return aliasMode ? aliasField : nameField;
    }
}
