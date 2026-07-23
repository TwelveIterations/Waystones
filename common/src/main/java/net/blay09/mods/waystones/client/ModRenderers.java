package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.color.block.BlockTintSources;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class ModRenderers {
    public static DeferredBlockStateModel waystoneRunesModel;
    public static DeferredBlockStateModel portstoneRunesModel;
    public static DeferredBlockStateModel sharestoneRunesModel;

    public static void initialize(BalmBlockStateModelRegistrar models) {
        waystoneRunesModel = models.register(id("block/waystone_runes"));
        portstoneRunesModel = models.register(id("block/portstone_runes"));
        sharestoneRunesModel = models.register(id("block/sharestone_runes"));
    }

    public static void initialize(BalmBlockEntityRendererRegistrar renderers) {
        renderers.register(ModBlockEntities.waystone, WaystoneRenderer::new);
        renderers.register(ModBlockEntities.sharestone, SharestoneRenderer::new);
        renderers.register(ModBlockEntities.portstone, PortstoneRenderer::new);
        renderers.register(ModBlockEntities.fleetingMemorial, FleetingMemorialRenderer::new);
    }

    public static void initialize(BalmBlockColorRegistrar blockColors) {
        blockColors.register(List.of(BlockTintSources.constant(0xffc456bd)), ModBlocks.warpPlate);
        blockColors.register(List.of(state -> ((SharestoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.sharestones.values());
        blockColors.register(List.of(state -> ((PortstoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.portstones.values());
    }

}
