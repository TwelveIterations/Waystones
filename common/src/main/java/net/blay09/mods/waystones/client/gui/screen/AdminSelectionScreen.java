package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

public class AdminSelectionScreen extends WaystoneSelectionScreenBase {

    public AdminSelectionScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
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

    @Override
    protected @Nullable Comparator<Waystone> getManualSorting() {
        return null;
    }
}
