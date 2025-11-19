package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.Identifier;

public class ModModels {
    public static void initialize(BalmBlockStateModelRegistrar models) {
        // waystoneRunes = models.loadModel(id("block/waystone_runes"));
        // sharestoneRunes = models.loadModel(id("block/sharestone_runes"));
        // portstoneRunes = models.loadModel(id("block/portstone_runes"));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Waystones.MOD_ID, path);
    }
}