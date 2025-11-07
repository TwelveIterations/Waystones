package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static Holder<BlockEntityType<WaystoneBlockEntity>> waystone;
    public static Holder<BlockEntityType<SharestoneBlockEntity>> sharestone;
    public static Holder<BlockEntityType<WarpPlateBlockEntity>> warpPlate;
    public static Holder<BlockEntityType<PortstoneBlockEntity>> portstone;

    public static void initialize(BalmBlockEntityTypeRegistrar blockEntities) {
        waystone = blockEntities.register("waystone",
                WaystoneBlockEntity::new,
                ModBlocks.waystone, ModBlocks.mossyWaystone, ModBlocks.sandyWaystone, ModBlocks.deepslateWaystone, ModBlocks.blackstoneWaystone, ModBlocks.endStoneWaystone).asHolder();
        sharestone = blockEntities.register("sharestone", SharestoneBlockEntity::new, ModBlocks.sharestones.values()).asHolder();
        warpPlate = blockEntities.register("warp_plate", WarpPlateBlockEntity::new, ModBlocks.warpPlate).asHolder();
        portstone = blockEntities.register("portstone", PortstoneBlockEntity::new, ModBlocks.portstones.values()).asHolder();
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

}
