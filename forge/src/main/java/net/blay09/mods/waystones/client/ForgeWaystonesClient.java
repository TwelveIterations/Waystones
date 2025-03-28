package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;

public class ForgeWaystonesClient {

    public static void initialize() {
        BalmClient.registerModule(new WaystonesClient());
    }
}
