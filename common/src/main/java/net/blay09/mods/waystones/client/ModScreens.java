package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.gui.screens.inventory.BalmMenuScreenRegistrar;
import net.blay09.mods.waystones.client.gui.screen.*;
import net.blay09.mods.waystones.menu.ModMenus;

public class ModScreens {
    public static void initialize(BalmMenuScreenRegistrar screens) {
        screens.register(ModMenus.waystoneSelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.warpScrollSelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.portalScrollSelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.warpStoneSelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.portstoneSelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.inventorySelection, WaystoneSelectionScreen::new);
        screens.register(ModMenus.sharestoneSelection, SharestoneSelectionScreen::new);
        screens.register(ModMenus.waystoneModifiers, WaystoneModifierScreen::new);
        screens.register(ModMenus.waystoneSettings, WaystoneEditScreen::new);
        screens.register(ModMenus.personalWaystoneSettings, PersonalWaystoneSettingsScreen::new);
        screens.register(ModMenus.adminSelection, AdminSelectionScreen::new);
    }
}
