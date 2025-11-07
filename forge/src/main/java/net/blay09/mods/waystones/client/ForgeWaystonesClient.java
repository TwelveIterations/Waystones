package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;

public class ForgeWaystonesClient {

    public static void initialize(BalmClientRegistrars registrars) {
        registrars.registerModule(new WaystonesClient());
    }
}
