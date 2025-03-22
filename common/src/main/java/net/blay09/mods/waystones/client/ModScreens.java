package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.waystones.client.gui.screen.*;
import net.blay09.mods.waystones.menu.ModMenus;

import static net.blay09.mods.waystones.Waystones.id;

public class ModScreens {
    public static void initialize(BalmScreens screens) {
        screens.registerScreen(id("waystone_selection"), ModMenus.waystoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(id("warp_scroll_selection"), ModMenus.warpScrollSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(id("warp_stone_selection"), ModMenus.warpStoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(id("portstone_selection"), ModMenus.portstoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(id("inventory_selection"), ModMenus.inventorySelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(id("sharestone_selection"), ModMenus.sharestoneSelection::get, SharestoneSelectionScreen::new);
        screens.registerScreen(id("waystone_modifiers"), ModMenus.waystoneModifiers::get, WaystoneModifierScreen::new);
        screens.registerScreen(id("waystone_settings"), ModMenus.waystoneSettings::get, WaystoneEditScreen::new);
        screens.registerScreen(id("admin_selection"), ModMenus.adminSelection::get, AdminSelectionScreen::new);
    }
}
