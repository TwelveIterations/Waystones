package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.menu.PersonalWaystoneSettingsMenu;
import net.blay09.mods.waystones.network.message.ServerboundPersonalWaystoneSettingsPacket;
import net.blay09.mods.waystones.network.message.ServerboundRequestEditWaystonePacket;
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
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

import static net.blay09.mods.waystones.Waystones.id;

public class PersonalWaystoneSettingsScreen extends AbstractContainerScreen<PersonalWaystoneSettingsMenu> {

    private @Nullable EditBox aliasField;

    public PersonalWaystoneSettingsScreen(PersonalWaystoneSettingsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 210);
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
                    _ -> {
                        savePersonalWaystoneSettings();
                        Balm.networking().sendToServer(new ServerboundRequestEditWaystonePacket(menu.getWaystone().getPos()));
                    },
                    editButtonLabel);
            editButton.setPosition(leftPos + 155, y);
            editButton.setTooltip(Tooltip.create(editButtonLabel));
            addRenderableWidget(editButton);
        }

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
        if (aliasField != null && aliasField.isMouseOver(event.x(), event.y()) && event.button() == 1) {
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
        super.onClose();
    }

    private void savePersonalWaystoneSettings() {
        final var currentAlias = menu.getAlias() != null ? menu.getAlias().getString() : "";
        if (aliasField != null && !aliasField.getValue().equals(currentAlias)) {
            Balm.networking()
                    .sendToServer(new ServerboundPersonalWaystoneSettingsPacket(
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
