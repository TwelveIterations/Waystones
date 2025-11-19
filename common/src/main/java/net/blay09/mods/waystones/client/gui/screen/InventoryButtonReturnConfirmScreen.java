package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.message.ServerboundInventoryButtonPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class InventoryButtonReturnConfirmScreen extends ConfirmScreen {

    private final Component waystoneName;

    public InventoryButtonReturnConfirmScreen() {
        this("");
    }

    public InventoryButtonReturnConfirmScreen(String targetWaystone) {
        super(result -> {
            if (result) {
                Balm.networking().sendToServer(ServerboundInventoryButtonPacket.INSTANCE);
            }
            Minecraft.getInstance().setScreen(null);
        }, Component.translatable("gui.waystones.inventory.confirm_return"), Component.empty());

        this.waystoneName = getWaystoneName(targetWaystone);
    }

    private static Component getWaystoneName(String targetWaystone) {
        if (!targetWaystone.isEmpty()) {
            return Component.translatable("gui.waystones.inventory.confirm_return_bound_to", targetWaystone).withStyle(ChatFormatting.GRAY);
        }

        final var player = Minecraft.getInstance().player;
        final var nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
        return nearestWaystone.map(Waystone::getName)
                .map(it -> Component.translatable("gui.waystones.inventory.confirm_return_bound_to", it).withStyle(ChatFormatting.GRAY))
                .orElse(Component.translatable("gui.waystones.inventory.no_waystones_activated").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, waystoneName, width / 2, 100, 0xFFFFFFFF);
    }
}
