package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.model.geom.BalmModelLayerRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class ModRenderers {
    public static ModelLayerLocation portstoneModel;
    public static ModelLayerLocation sharestoneModel;
    public static ModelLayerLocation waystoneModel;

    public static void initialize(BalmModelLayerRegistrar modelLayers) {
        portstoneModel = modelLayers.register(id("portstone"),
                () -> PortstoneModel.createLayer(CubeDeformation.NONE));
        sharestoneModel = modelLayers.register(id("sharestone"),
                () -> SharestoneModel.createLayer(CubeDeformation.NONE));
        waystoneModel = modelLayers.register(id("waystone"),
                () -> WaystoneModel.createLayer(CubeDeformation.NONE));
    }

    public static void initialize(BalmBlockEntityRendererRegistrar renderers) {
        renderers.register(ModBlockEntities.waystone, WaystoneRenderer::new);
        renderers.register(ModBlockEntities.sharestone, SharestoneRenderer::new);
        renderers.register(ModBlockEntities.portstone, PortstoneRenderer::new);
    }

    public static void initialize(BalmBlockColorRegistrar blockColors) {
        blockColors.register(List.of(BlockTintSources.constant(0xffc456bd)), ModBlocks.warpPlate);
        blockColors.register(List.of(state -> ((SharestoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.sharestones.values());
        blockColors.register(List.of(state -> ((PortstoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.portstones.values());
    }

}
