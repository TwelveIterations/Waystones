package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(dataOutput, provider);
    }

    @Override
    public void generate() {
        add(ModBlocks.waystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.waystone));
        add(ModBlocks.sandyWaystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.sandyWaystone));
        add(ModBlocks.mossyWaystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.mossyWaystone));
        add(ModBlocks.deepslateWaystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.deepslateWaystone));
        add(ModBlocks.blackstoneWaystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.blackstoneWaystone));
        add(ModBlocks.endStoneWaystone.asBlock(), createDoubleBlockWaystoneLoot(ModBlocks.endStoneWaystone));
        add(ModBlocks.warpPlate.asBlock(), createWaystoneLoot(ModBlocks.warpPlate));
        for (final var portstone : ModBlocks.portstones.values()) {
            add(portstone.asBlock(), createDoubleBlockWaystoneLoot(portstone));
        }
        for (final var sharestone : ModBlocks.sharestones.values()) {
            add(sharestone.asBlock(), createDoubleBlockWaystoneLoot(sharestone));
        }
    }

    private LootTable.Builder createWaystoneLoot(DeferredBlock block) {

        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))
                                .when(hasSilkTouch())));
    }

    private LootTable.Builder createDoubleBlockWaystoneLoot(DeferredBlock block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1f))
                        .add(LootItem.lootTableItem(block))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block.asBlock())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WaystoneBlockBase.HALF, DoubleBlockHalf.LOWER)))
                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                .when(hasSilkTouch()))));
    }
}
