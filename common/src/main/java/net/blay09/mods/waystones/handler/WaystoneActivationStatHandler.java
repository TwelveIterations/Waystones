package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.stats.ModStats;
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent;

public class WaystoneActivationStatHandler {

    public static void onWaystoneActivated(WaystoneActivatedEvent event) {
        event.player().awardStat(ModStats.waystoneActivated);
    }

}
