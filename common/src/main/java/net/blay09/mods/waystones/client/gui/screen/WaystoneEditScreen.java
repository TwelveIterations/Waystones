package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.client.gui.widget.WaystoneVisbilityButton;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.blay09.mods.waystones.network.message.RequestManageWaystoneModifiersMessage;
import net.blay09.mods.waystones.network.message.RequestPersonalWaystoneSettingsMessage;
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

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneEditScreen extends AbstractContainerScreen<WaystoneEditMenu> {

    private @Nullable EditBox nameField;
    private @Nullable WaystoneVisbilityButton visibilityButton;
    private @Nullable ImageButton modifierButton;

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

        final var aliasButtonLabel = Component.translatable("gui.waystones.waystone_settings.configure_alias");
        final var aliasButtonSprites = new WidgetSprites(
                id("widgets/alias_button"),
                id("widgets/alias_button_highlighted"));
        final var aliasButton = new ImageButton(21,
                21,
                aliasButtonSprites,
                button -> {
                    saveWaystoneSettings();
                    Balm.getNetworking().sendToServer(new RequestPersonalWaystoneSettingsMessage(menu.getWaystone().getWaystoneUid()));
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
                    saveWaystoneSettings();
                    Balm.getNetworking().sendToServer(new RequestManageWaystoneModifiersMessage(menu.getWaystone().getPos()));
                },
                Component.translatable("gui.waystones.waystone_settings.manage_modifiers"));
        modifierButton.setPosition(leftPos, y);
        modifierButton.active = menu.canEdit();
        addRenderableWidget(modifierButton);
        y += 24;

        final var saveButton = Button.builder(
                        Component.translatable(menu.canEdit() ? "gui.waystones.waystone_settings.save" : "gui.waystones.waystone_settings.close"),
                        button -> onClose())
                .pos(leftPos + 176 / 2 - 50, y)
                .size(100, 20)
                .build();
        addRenderableWidget(saveButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (nameField != null && menu.canEdit() && nameField.isMouseOver(mouseX, mouseY) && button == 1) {
            nameField.setValue("");
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (nameField != null && (nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.isFocused())) {
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

        if (nameField != null && menu.canEdit() && nameField.getValue().isEmpty()) {
            guiGraphics.drawString(Minecraft.getInstance().font,
                    Component.translatable("waystones.untitled_waystone"),
                    nameField.getX() + 4,
                    nameField.getY() + 6,
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
        if (visibilityButton != null) {
            guiGraphics.drawString(font,
                    Component.translatable("gui.waystones.waystone_settings.visibility." + visibilityButton.getVisibility().name().toLowerCase(Locale.ROOT)),
                    24,
                    visibilityButton.getY() - topPos + 6,
                    0xFFFFFFFF,
                    true);
        }
        if (modifierButton != null) {
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
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
    }

    @Override
    public void onClose() {
        saveWaystoneSettings();
        super.onClose();
    }

    private void saveWaystoneSettings() {
        if (menu.canEdit()) {
            Balm.getNetworking()
                    .sendToServer(new EditWaystoneMessage(
                            menu.getWaystone().getWaystoneUid(),
                            nameField != null ? nameField.getValue() : menu.getWaystone().getName().getString(),
                            visibilityButton != null ? visibilityButton.getVisibility() : menu.getWaystone().getVisibility()));
        }
    }
}
