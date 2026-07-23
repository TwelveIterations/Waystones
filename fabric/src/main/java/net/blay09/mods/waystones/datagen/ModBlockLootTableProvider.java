package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlockBase;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
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

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
    protected ModBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(dataOutput, provider);
    }

    @Override
    public void generate() {
        for (final var waystone : ModBlocks.waystones.values()) {
            add(waystone.asBlock(), createDoubleBlockWaystoneLoot(waystone));
        }
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
