package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.waystones.client.gui.screen.SharestoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WarpPlateScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSettingsScreen;
import net.blay09.mods.waystones.menu.ModMenus;

public class ModScreens {
    public static void initialize(BalmScreens screens) {
        screens.registerScreen(ModMenus.waystoneSelection::get, WaystoneSelectionScreen::new);
        screens.registerScreen(ModMenus.sharestoneSelection::get, SharestoneSelectionScreen::new);
        screens.registerScreen(ModMenus.warpPlate::get, WarpPlateScreen::new);
        screens.registerScreen(ModMenus.waystoneSettings::get, WaystoneSettingsScreen::new);
    }
}
