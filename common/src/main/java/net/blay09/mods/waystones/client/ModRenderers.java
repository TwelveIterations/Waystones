package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class ModRenderers {
    public static void initialize(BalmRenderers renderers) {
        renderers.registerBlockEntityRenderer(ModBlockEntities.waystone::get, WaystoneRenderer::new);
        renderers.registerBlockEntityRenderer(ModBlockEntities.sharestone::get, SharestoneRenderer::new);
        renderers.registerBlockEntityRenderer(ModBlockEntities.portstone::get, PortstoneRenderer::new);
        renderers.registerBlockEntityRenderer(ModBlockEntities.fleetingMemorial::get, FleetingMemorialRenderer::new);

        renderers.registerBlockColorHandler((state, view, pos, tintIndex) -> 0xffc456bd,
                () -> new Block[]{ModBlocks.warpPlate});
        renderers.registerItemColorHandler((itemStack, tintIndex) -> 0xffc456bd, () -> new Item[]{ModBlocks.warpPlate.asItem()});
        renderers.registerBlockColorHandler((state, view, pos, tintIndex) -> Objects.requireNonNull(((SharestoneBlock) state.getBlock()).getColor())
                .getTextColor() | 0xFF000000, () -> ModBlocks.sharestones);
        renderers.registerItemColorHandler((stack, tintIndex) -> Objects.requireNonNull(((SharestoneBlock) Block.byItem((stack.getItem()))).getColor())
                .getTextColor() | 0xFF000000, () -> ModBlocks.sharestones);
        renderers.registerBlockColorHandler((state, view, pos, tintIndex) -> Objects.requireNonNull(((PortstoneBlock) state.getBlock()).getColor())
                .getTextColor() | 0xFF000000, () -> ModBlocks.portstones);
        renderers.registerItemColorHandler((stack, tintIndex) -> Objects.requireNonNull(((PortstoneBlock) Block.byItem((stack.getItem()))).getColor())
                .getTextColor() | 0xFF000000, () -> ModBlocks.portstones);

        renderers.setBlockRenderType(() -> ModBlocks.warpPlate, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.warpPortal, RenderType.translucent());
        renderers.setBlockRenderType(() -> ModBlocks.fleetingMemorial, RenderType.cutout());
    }

}
