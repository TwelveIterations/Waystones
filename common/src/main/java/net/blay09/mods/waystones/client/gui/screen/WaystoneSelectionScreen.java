package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class WaystoneSelectionScreen extends WaystoneSelectionScreenBase {

    public WaystoneSelectionScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

}
