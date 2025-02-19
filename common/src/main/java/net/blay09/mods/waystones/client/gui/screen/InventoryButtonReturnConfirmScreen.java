package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.InventoryButtonMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class InventoryButtonReturnConfirmScreen extends ConfirmScreen {

    private final String waystoneName;

    public InventoryButtonReturnConfirmScreen() {
        this("");
    }

    public InventoryButtonReturnConfirmScreen(String targetWaystone) {
        super(result -> {
            if (result) {
                Balm.getNetworking().sendToServer(new InventoryButtonMessage());
            }
            Minecraft.getInstance().setScreen(null);
        }, Component.translatable("gui.waystones.inventory.confirm_return"), Component.empty());

        this.waystoneName = getWaystoneName(targetWaystone);
    }

    private static String getWaystoneName(String targetWaystone) {
        if (!targetWaystone.isEmpty()) {
            return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return_bound_to", targetWaystone);
        }

        IWaystone nearestWaystone = PlayerWaystoneManager.getNearestWaystone(Minecraft.getInstance().player);
        if (nearestWaystone != null) {
            return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return_bound_to", nearestWaystone.getName());
        }

        return ChatFormatting.GRAY + I18n.get("gui.waystones.inventory.confirm_return.noWaystoneActive");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, waystoneName, width / 2, 100, 0xFFFFFF);
    }
}
