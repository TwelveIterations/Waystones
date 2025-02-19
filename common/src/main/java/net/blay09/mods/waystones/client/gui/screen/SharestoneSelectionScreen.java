package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SharestoneSelectionScreen extends WaystoneSelectionScreenBase {

    public SharestoneSelectionScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected boolean allowSorting() {
        return false;
    }

    @Override
    protected boolean allowDeletion() {
        return false;
    }
}
