package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<WaystoneBlockEntity>> waystone;
    public static DeferredObject<BlockEntityType<SharestoneBlockEntity>> sharestone;
    public static DeferredObject<BlockEntityType<WarpPlateBlockEntity>> warpPlate;
    public static DeferredObject<BlockEntityType<PortstoneBlockEntity>> portstone;

    public static void initialize(BalmBlockEntities blockEntities) {
        waystone = blockEntities.registerBlockEntity(id("waystone"), WaystoneBlockEntity::new, () -> new Block[]{ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone});
        sharestone = blockEntities.registerBlockEntity(id("sharestone"), SharestoneBlockEntity::new, () -> ArrayUtils.add(ModBlocks.scopedSharestones, ModBlocks.sharestone));
        warpPlate = blockEntities.registerBlockEntity(id("warp_plate"), WarpPlateBlockEntity::new, () -> new Block[] {ModBlocks.warpPlate});
        portstone = blockEntities.registerBlockEntity(id("portstone"), PortstoneBlockEntity::new, () -> new Block[] {ModBlocks.portstone});
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
