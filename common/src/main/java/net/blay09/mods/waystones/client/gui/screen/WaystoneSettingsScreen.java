package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.menu.WaystoneSettingsMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneTypes;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class WaystoneSettingsScreen extends AbstractContainerScreen<WaystoneSettingsMenu> {

    private final MutableComponent isGlobalText = Component.translatable("gui.waystones.waystone_settings.is_global");

    private EditBox textField;
    private Button doneButton;
    private Checkbox isGlobalCheckbox;

    private boolean focusTextFieldNextTick;

    public WaystoneSettingsScreen(WaystoneSettingsMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        imageWidth = 270;
        imageHeight = 200;
    }

    @Override
    public void init() {
        // Leave no space for JEI!
        imageWidth = width;

        super.init();
        IWaystone waystone = menu.getWaystone();
        String oldText = waystone.getName();
        if (textField != null) {
            oldText = textField.getValue();
        }

        textField = new EditBox(Minecraft.getInstance().font, width / 2 - 100, height / 2 - 20, 200, 20, textField, Component.empty());
        textField.setMaxLength(128);
        textField.setValue(oldText);
        addRenderableWidget(textField);
        setInitialFocus(textField);

        doneButton = Button.builder(Component.translatable("gui.done"), button -> {
            if (textField.getValue().isEmpty()) {
                focusTextFieldNextTick = true;
                return;
            }

            Balm.getNetworking().sendToServer(new EditWaystoneMessage(waystone.getWaystoneUid(), textField.getValue(), isGlobalCheckbox.selected()));
        }).pos(width / 2, height / 2 + 10).size(100, 20).build();
        addRenderableWidget(doneButton);

        isGlobalCheckbox = new Checkbox(width / 2 - 100, height / 2 + 10, 20, 20, Component.empty(), waystone.isGlobal());
        isGlobalCheckbox.visible = waystone.getWaystoneType()
                .equals(WaystoneTypes.WAYSTONE) && PlayerWaystoneManager.mayEditGlobalWaystones(Objects.requireNonNull(Minecraft.getInstance().player));
        addRenderableWidget(isGlobalCheckbox);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final int chkGlobalLabelX = width / 2 - 100 + 25;
        final int chkGlobalLabelY = height / 2 + 16;
        final int chkGlobalLabelWidth = minecraft.font.width(I18n.get("gui.waystones.waystone_settings.is_global"));
        if (mouseX >= chkGlobalLabelX && mouseX < chkGlobalLabelX + chkGlobalLabelWidth && mouseY >= chkGlobalLabelY && mouseY < chkGlobalLabelY + minecraft.font.lineHeight) {
            isGlobalCheckbox.onPress();
            return true;
        }

        if (textField.isMouseOver(mouseX, mouseY) && button == 1) {
            textField.setValue("");
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            doneButton.onPress();
            return true;
        }

        if (textField.keyPressed(keyCode, scanCode, modifiers) || textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                Objects.requireNonNull(minecraft.player).closeContainer();
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void containerTick() {
        textField.tick();

        // Button presses focus the button after onPress, so we can't change focus inside. Defer to here instead.
        if (focusTextFieldNextTick) {
            setInitialFocus(textField);
            focusTextFieldNextTick = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawString(font, getTitle(), width / 2 - 100, height / 2 - 35, 0xFFFFFF);

        if (isGlobalCheckbox.visible) {
            guiGraphics.drawString(font, isGlobalText, width / 2 - 100 + 25, height / 2 + 16, 0xFFFFFF);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
