package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.waystones.config.WaystonesConfigData;

public class SyncWaystonesConfigMessage extends SyncConfigMessage<WaystonesConfigData> {
    public SyncWaystonesConfigMessage(WaystonesConfigData data) {
        super(data);
    }
}
