package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static Holder<BlockEntityType<WaystoneBlockEntity>> waystone;
    public static Holder<BlockEntityType<SharestoneBlockEntity>> sharestone;
    public static Holder<BlockEntityType<WarpPlateBlockEntity>> warpPlate;
    public static Holder<BlockEntityType<WarpPortalBlockEntity>> warpPortal;
    public static Holder<BlockEntityType<PortstoneBlockEntity>> portstone;

    public static void initialize(BalmBlockEntityTypeRegistrar blockEntities) {
        waystone = blockEntities.register("waystone",
                WaystoneBlockEntity::new,
                ModBlocks.waystones.values()).asHolder();
        sharestone = blockEntities.register("sharestone", SharestoneBlockEntity::new, ModBlocks.sharestones.values()).asHolder();
        warpPlate = blockEntities.register("warp_plate", WarpPlateBlockEntity::new, ModBlocks.warpPlate).asHolder();
        warpPortal = blockEntities.register("warp_portal", WarpPortalBlockEntity::new, ModBlocks.warpPortal).asHolder();
        portstone = blockEntities.register("portstone", PortstoneBlockEntity::new, ModBlocks.portstones.values()).asHolder();
    }
}
