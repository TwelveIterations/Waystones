package net.blay09.mods.waystones.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.migration.ConfigMigration;
import net.blay09.mods.waystones.network.message.SyncWaystonesConfigMessage;

public class WaystonesConfig {

    public static WaystonesConfigData getActive() {
        return Balm.getConfig().getActive(WaystonesConfigData.class);
    }

    public static void initialize() {
        final var config = Balm.getConfig();
        config.registerConfig(WaystonesConfigData.class, SyncWaystonesConfigMessage::new);

        final var schema = config.getSchema(WaystonesConfigData.class);
        config.onConfigAvailable(schema, ignored -> {
            final var waystonesConfig = config.getActiveConfig(WaystonesConfigData.class);
            if (waystonesConfig != null && ConfigMigration.needsWarpRequirementsDefaultMigration(waystonesConfig)) {
                config.updateLocalConfig(WaystonesConfigData.class, ConfigMigration::migrateWarpRequirementsDefault);
            }
        });
    }
}
