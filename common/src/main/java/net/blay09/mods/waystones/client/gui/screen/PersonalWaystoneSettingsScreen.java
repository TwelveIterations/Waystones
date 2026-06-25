package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
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
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static net.blay09.mods.waystones.Waystones.id;

public class PersonalWaystoneSettingsScreen extends AbstractContainerScreen<PersonalWaystoneSettingsMenu> {

    private @Nullable EditBox aliasField;

    public PersonalWaystoneSettingsScreen(PersonalWaystoneSettingsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 210;
        titleLabelY = 44;
    }

    @Override
    public void init() {
        super.init();

        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";
        final var oldAliasText = aliasField != null ? aliasField.getValue() : currentAlias;
        final var y = topPos + titleLabelY + 16;
        final var canEditWaystone = isWaystoneInRange();
        final var aliasFieldWidth = canEditWaystone ? 150 : 176;

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
        if (aliasField != null && !aliasField.getValue().equals(currentAlias)) {
            Balm.getNetworking()
                    .sendToServer(new UserDecorateWaystoneMessage(
                            menu.getWaystone().getWaystoneUid(),
                            aliasField.getValue().trim().isEmpty() ? Optional.empty() : Optional.of(Component.literal(aliasField.getValue()))));
        }
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
