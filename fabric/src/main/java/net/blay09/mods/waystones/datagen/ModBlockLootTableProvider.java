package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(dataOutput, provider);
    }

    @Override
    public void generate() {
        add(ModBlocks.waystone, createDoubleBlockWaystoneLoot(ModBlocks.waystone));
        add(ModBlocks.sandyWaystone, createDoubleBlockWaystoneLoot(ModBlocks.sandyWaystone));
        add(ModBlocks.mossyWaystone, createDoubleBlockWaystoneLoot(ModBlocks.mossyWaystone));
        add(ModBlocks.deepslateWaystone, createDoubleBlockWaystoneLoot(ModBlocks.deepslateWaystone));
        add(ModBlocks.blackstoneWaystone, createDoubleBlockWaystoneLoot(ModBlocks.blackstoneWaystone));
        add(ModBlocks.endStoneWaystone, createDoubleBlockWaystoneLoot(ModBlocks.endStoneWaystone));
        add(ModBlocks.redNetherBricksWaystone, createDoubleBlockWaystoneLoot(ModBlocks.redNetherBricksWaystone));
        add(ModBlocks.purpurWaystone, createDoubleBlockWaystoneLoot(ModBlocks.purpurWaystone));
        add(ModBlocks.prismarineWaystone, createDoubleBlockWaystoneLoot(ModBlocks.prismarineWaystone));
        add(ModBlocks.mudBricksWaystone, createDoubleBlockWaystoneLoot(ModBlocks.mudBricksWaystone));
        add(ModBlocks.warpPlate, createWaystoneLoot(ModBlocks.warpPlate));
        for (final var portstone : ModBlocks.portstones) {
            add(portstone, createDoubleBlockWaystoneLoot(portstone));
        }
        for (final var sharestone : ModBlocks.sharestones) {
            add(sharestone, createDoubleBlockWaystoneLoot(sharestone));
        }
    }

    private LootTable.Builder createWaystoneLoot(Block block) {

        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .when(hasSilkTouch())))
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)
                                .when(hasSilkTouch())));
    }

    private LootTable.Builder createDoubleBlockWaystoneLoot(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WaystoneBlockBase.HALF, DoubleBlockHalf.LOWER)))
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .when(hasSilkTouch()))));
    }
}
