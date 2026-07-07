package net.blay09.mods.waystones.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class WaystoneContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private boolean suppressContainerClose;

    protected WaystoneContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected void openSiblingScreen(Screen screen) {
        suppressNextContainerClose();
        Minecraft.getInstance().setScreen(screen);
    }

    protected void suppressNextContainerClose() {
        suppressContainerClose = true;
    }

    @Override
    public void removed() {
        if (suppressContainerClose) {
            suppressContainerClose = false;
            return;
        }

        super.removed();
    }
}
