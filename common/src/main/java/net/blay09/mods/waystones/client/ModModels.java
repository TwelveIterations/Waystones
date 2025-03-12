package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;

public class ModModels {
    public static void initialize(BalmModels models) {
        // waystoneRunes = models.loadModel(id("block/waystone_runes"));
        // sharestoneRunes = models.loadModel(id("block/sharestone_runes"));
        // portstoneRunes = models.loadModel(id("block/portstone_runes"));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, path);
    }
}