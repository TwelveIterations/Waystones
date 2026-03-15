package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.PortstoneType;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.blay09.mods.waystones.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

import static net.blay09.mods.waystones.Waystones.id;
import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class ModModelProvider extends FabricModelProvider {

    private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
            .select(Direction.EAST, Y_ROT_90)
            .select(Direction.SOUTH, Y_ROT_180)
            .select(Direction.WEST, Y_ROT_270)
            .select(Direction.NORTH, NOP);

    public ModModelProvider(FabricPackOutput output) {
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
        for (final var entry : ModBlocks.waystones.entrySet()) {
            createDoubleBlockWaystone(blockStateModelGenerator, entry.getKey(), entry.getValue().asBlock());
        }
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
        ModItems.warpStones.forEach((_, warpStone) -> itemModelGenerator.generateFlatItem(warpStone.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM));
        itemModelGenerator.generateFlatItem(ModItems.warpScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.returnScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.boundScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.blankScroll.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void createDoubleBlockWaystone(BlockModelGenerators blockStateModelGenerator,  WaystoneType type, Block block) {
        final var itemModelTemplate = new ModelTemplate(Optional.of(id("waystone").withPrefix("item/")), Optional.empty(), TextureSlot.TEXTURE);
        final var topModelTemplate = new ModelTemplate(Optional.of(id("waystone_top").withPrefix("block/")), Optional.of("_top"), TextureSlot.TEXTURE, TextureSlot.PARTICLE);
        final var bottomModelTemplate = new ModelTemplate(Optional.of(id("waystone_bottom").withPrefix("block/")), Optional.of("_bottom"), TextureSlot.TEXTURE, TextureSlot.PARTICLE);
        final var textureMapping = TextureMapping.defaultTexture(block).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(type.particleBlock()));
        final var itemModel = itemModelTemplate.create(block.asItem(), textureMapping, blockStateModelGenerator.modelOutput);
        final var topModel = topModelTemplate.create(block, textureMapping, blockStateModelGenerator.modelOutput);
        final var bottomModel = bottomModelTemplate.create(block, textureMapping, blockStateModelGenerator.modelOutput);
        final var generator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModel))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModel)))
                .with(PropertyDispatch.modify(WaystoneBlock.SEEN)
                        .select(false, NOP)
                        .select(true, NOP))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        blockStateModelGenerator.registerSimpleItemModel(block, itemModel);
    }

    private void createSharestone(BlockModelGenerators blockStateModelGenerator, SharestoneType type, DeferredBlock block) {
        final var topModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/sharestone_top");
        final var bottomModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/sharestone_bottom");
        final var generator = MultiVariantGenerator.dispatch(block.asBlock())
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModelLocation)))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        final var itemModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "item/sharestone");
        blockStateModelGenerator.registerSimpleTintedItemModel(block.asBlock(), itemModelLocation, new Constant(type.textColor()));
    }

    private void createPortstone(BlockModelGenerators blockStateModelGenerator, PortstoneType type, DeferredBlock block) {
        final var topModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/portstone_top");
        final var bottomModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "block/portstone_bottom");
        final var generator = MultiVariantGenerator.dispatch(block.asBlock())
                .with(PropertyDispatch.initial(WaystoneBlockBase.HALF)
                        .select(DoubleBlockHalf.LOWER, plainVariant(bottomModelLocation))
                        .select(DoubleBlockHalf.UPPER, plainVariant(topModelLocation)))
                .with(ROTATION_HORIZONTAL_FACING);
        blockStateModelGenerator.blockStateOutput.accept(generator);
        final var itemModelLocation = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "item/portstone");
        blockStateModelGenerator.registerSimpleTintedItemModel(block.asBlock(), itemModelLocation, new Constant(type.textColor()));
    }

}
