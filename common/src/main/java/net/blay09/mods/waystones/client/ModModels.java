package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ModModels {
    public static DeferredObject<BakedModel> waystoneRunes;
    public static DeferredObject<BakedModel> sharestoneRunes;
    public static DeferredObject<BakedModel> portstoneRunes;

    public static final Map<Block, DeferredObject<BakedModel>> waystoneRunesModels = new HashMap<>();
    public static final Map<Block, DeferredObject<BakedModel>> sharestoneRunesModels = new HashMap<>();
    public static final Map<Block, DeferredObject<BakedModel>> portstoneRunesModels = new HashMap<>();

    public static void initialize(BalmModels models) {
        waystoneRunes = models.loadModel(id("block/waystone_runes"));
        sharestoneRunes = models.loadModel(id("block/sharestone_runes"));
        portstoneRunes = models.loadModel(id("block/portstone_runes"));
        waystoneRunesModels.put(ModBlocks.waystone, waystoneRunes);
        for (final var block : new Block[]{ModBlocks.mossyWaystone, ModBlocks.sandyWaystone, ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone, ModBlocks.endStoneWaystone, ModBlocks.redNetherBricksWaystone,
                ModBlocks.purpurWaystone, ModBlocks.prismarineWaystone, ModBlocks.mudBricksWaystone}) {
            waystoneRunesModels.put(block, models.loadModel(BuiltInRegistries.BLOCK.getKey(block).withPath(path -> "block/" + path + "_runes")));
        }
        for (final var block : ModBlocks.sharestones) {
            sharestoneRunesModels.put(block, models.loadModel(BuiltInRegistries.BLOCK.getKey(block).withPath(path -> "block/" + path + "_runes")));
        }
        for (final var block : ModBlocks.portstones) {
            portstoneRunesModels.put(block, models.loadModel(BuiltInRegistries.BLOCK.getKey(block).withPath(path -> "block/" + path + "_runes")));
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, path);
    }
}
