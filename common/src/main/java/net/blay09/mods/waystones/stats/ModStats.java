package net.blay09.mods.waystones.stats;

import net.blay09.mods.balm.stats.BalmCustomStatRegistrar;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;

public class ModStats {

    public static final Identifier waystoneActivated = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "waystone_activated");

    public static void initialize(BalmCustomStatRegistrar stats) {
        stats.register(waystoneActivated, StatFormatter.DEFAULT);
    }

}
