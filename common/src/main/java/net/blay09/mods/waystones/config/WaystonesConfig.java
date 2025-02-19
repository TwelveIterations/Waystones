package net.blay09.mods.waystones.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.network.message.SyncWaystonesConfigMessage;

public class WaystonesConfig {

    public static WaystonesConfigData getActive() {
        return Balm.getConfig().getActive(WaystonesConfigData.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(WaystonesConfigData.class, SyncWaystonesConfigMessage::new);
    }
}
