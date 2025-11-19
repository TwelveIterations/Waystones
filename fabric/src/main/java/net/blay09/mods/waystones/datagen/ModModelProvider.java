package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.*;
import net.blay09.mods.waystones.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import static net.blay09.mods.waystones.Waystones.id;
import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class ModModelProvider extends FabricModelProvider {

    private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
            .select(Direction.EAST, Y_ROT_90)
            .select(Direction.SOUTH, Y_ROT_180)
            .select(Direction.WEST, Y_ROT_270)
            .select(Direction.NORTH, NOP);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.warpPlate.asBlock())
                .with(PropertyDispatch.initial(WarpPlateBlock.STATUS)
                        .select(WarpPlateBlock.WarpPlateStatus.EMPTY, plainVariant(id("block/warp_plate_empty")))
                        .select(WarpPlateBlock.WarpPlateStatus.IDLE, plainVariant(id("block/warp_plate")))
                        .select(WarpPlateBlock.WarpPlateStatus.ATTUNING, plainVariant(id("block/warp_plate")))
                        .select(WarpPlateBlock.WarpPlateStatus.WARPING, plainVariant(id("block/warp_plate")))
                        .select(WarpPlateBlock.WarpPlateStatus.WARPING_INVALID, plainVariant(id("block/warp_plate")))
                        .select(WarpPlateBlock.WarpPlateStatus.LOCKED, plainVariant(id("block/warp_plate_locked")))
                ));
        blockStateModelGenerator.registerSimpleTintedItemModel(ModBlocks.warpPlate.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.warpPlate.asBlock()), new Constant(0xffc456bd));
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.waystone.asBlock());
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.sandyWaystone.asBlock());
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.mossyWaystone.asBlock());
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.deepslateWaystone.asBlock());
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.blackstoneWaystone.asBlock());
        createDoubleBlockWaystone(blockStateModelGenerator, ModBlocks.endStoneWaystone.asBlock());
        for (final var entry : ModBlocks.portstones.entrySet()) {
            createPortstone(blockStateModelGenerator, entry.getKey(), entry.getValue());
        }
        for (final var entry : ModBlocks.sharestones.entrySet()) {
            createSharestone(blockStateModelGenerator, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.dormantShard.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.attunedShard.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.crumblingAttunedShard.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.deepslateShard.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.warpStone.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.warpScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.returnScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.boundScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.blankScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void createDoubleBlockWaystone(BlockModelGenerators blockStateModelGenerator, Block block) {
        createDoubleBlockWaystone(blockStateModelGenerator, block, block);
    }

    private void createDoubleBlockWaystone(BlockModelGenerators blockStateModelGenerator, Block block, Block modelBlock) {
        final var topModelLocation = ModelLocationUtils.getModelLocation(modelBlock, "_top");
        final var bottomModelLocation = ModelLocationUtils.getModelLocation(modelBlock, "_bottom");
        final var generator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModelLocation)))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        blockStateModelGenerator.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block.asItem()));
    }

    private void createSharestone(BlockModelGenerators blockStateModelGenerator, DyeColor color, DeferredBlock block) {
        final var topModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/sharestone_top");
        final var bottomModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/sharestone_bottom");
        final var generator = MultiVariantGenerator.dispatch(block.asBlock())
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModelLocation)))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        final var itemModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "item/sharestone");
        blockStateModelGenerator.registerSimpleTintedItemModel(block.asBlock(), itemModelLocation, new Constant(color.getTextColor()));
    }

    private void createPortstone(BlockModelGenerators blockStateModelGenerator, DyeColor color, DeferredBlock block) {
        final var topModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/portstone_top");
        final var bottomModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/portstone_bottom");
        final var generator = MultiVariantGenerator.dispatch(block.asBlock())
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModelLocation)))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        final var itemModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "item/portstone");
        blockStateModelGenerator.registerSimpleTintedItemModel(block.asBlock(), itemModelLocation, new Constant(color.getTextColor()));
    }

}
