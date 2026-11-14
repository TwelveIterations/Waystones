package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.api.PortstoneType;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.color.block.BlockTintSources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public class ModRenderers {
    public static DeferredBlockStateModel waystoneRunesModel;
    public static final Map<WaystoneType, DeferredBlockStateModel> waystoneRunesModels = new HashMap<>();
    public static DeferredBlockStateModel portstoneRunesModel;
    public static final Map<PortstoneType, DeferredBlockStateModel> portstoneRunesModels = new HashMap<>();
    public static DeferredBlockStateModel sharestoneRunesModel;
    public static final Map<SharestoneType, DeferredBlockStateModel> sharestoneRunesModels = new HashMap<>();

    public static void initialize(BalmBlockStateModelRegistrar models) {
        waystoneRunesModel = models.register(id("block/waystone_runes"));
        WaystoneType.values().forEach(type -> waystoneRunesModels.put(type,
                models.register(type.identifier().withPath(path -> "block/" + path + "_runes"))));
        portstoneRunesModel = models.register(id("block/portstone_runes"));
        PortstoneType.values().forEach(type -> portstoneRunesModels.put(type,
                models.register(type.identifier().withPath(path -> "block/" + path + "_portstone_runes"))));
        sharestoneRunesModel = models.register(id("block/sharestone_runes"));
        SharestoneType.values().forEach(type -> sharestoneRunesModels.put(type,
                models.register(type.identifier().withPath(path -> "block/" + path + "_runes"))));
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
