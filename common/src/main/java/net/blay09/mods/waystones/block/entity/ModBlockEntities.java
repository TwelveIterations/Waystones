package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    private static final BalmBlockEntities blockEntities = Balm.getBlockEntities();
    public static DeferredObject<BlockEntityType<WaystoneBlockEntity>> waystone = blockEntities.registerBlockEntity(id("waystone"),
            WaystoneBlockEntity::new,
            () -> new Block[]{
                    ModBlocks.waystone,
                    ModBlocks.mossyWaystone,
                    ModBlocks.sandyWaystone,
                    ModBlocks.deepslateWaystone,
                    ModBlocks.blackstoneWaystone,
                    ModBlocks.endStoneWaystone,
                    ModBlocks.redNetherBricksWaystone,
                    ModBlocks.purpurWaystone,
                    ModBlocks.prismarineWaystone,
                    ModBlocks.mudBricksWaystone});
    public static DeferredObject<BlockEntityType<SharestoneBlockEntity>> sharestone = blockEntities.registerBlockEntity(id("sharestone"),
            SharestoneBlockEntity::new,
            () -> ModBlocks.sharestones);
    public static DeferredObject<BlockEntityType<WarpPlateBlockEntity>> warpPlate = blockEntities.registerBlockEntity(id("warp_plate"),
            WarpPlateBlockEntity::new,
            () -> new Block[]{ModBlocks.warpPlate});
    public static DeferredObject<BlockEntityType<WarpPortalBlockEntity>> warpPortal = blockEntities.registerBlockEntity(id("warp_portal"),
            WarpPortalBlockEntity::new,
            () -> new Block[]{ModBlocks.warpPortal});
    public static DeferredObject<BlockEntityType<FleetingMemorialBlockEntity>> fleetingMemorial = blockEntities.registerBlockEntity(id("fleeting_memorial"),
            FleetingMemorialBlockEntity::new,
            () -> new Block[]{ModBlocks.fleetingMemorial});
    public static DeferredObject<BlockEntityType<PortstoneBlockEntity>> portstone = blockEntities.registerBlockEntity(id("portstone"),
            PortstoneBlockEntity::new,
            () -> ModBlocks.portstones);

    public static void initialize() {
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

}
