package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;

public class WaystoneActivationStatHandler {

    public static void onWaystoneActivated(WaystoneActivatedEvent event) {
        event.getPlayer().awardStat(ModStats.waystoneActivated);
    }

}
