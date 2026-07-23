package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        final var enchantableDurabilityTag = builder(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("enchantable/durability")));
        ModItems.warpStones.sortedValues().map(DeferredItem::asResourceKey).forEach(enchantableDurabilityTag::add);
        builder(ModItemTags.SCROLLS).add(ModItems.warpScroll.asResourceKey(), ModItems.portalScroll.asResourceKey(), ModItems.returnScroll.asResourceKey(), ModItems.boundScroll.asResourceKey(), ModItems.blankScroll.asResourceKey());
        builder(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll.asResourceKey());
        builder(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll.asResourceKey());
        builder(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll.asResourceKey());
        builder(ModItemTags.PORTAL_SCROLLS).add(ModItems.portalScroll.asResourceKey());
        final var warpStonesTag = builder(ModItemTags.WARP_STONES);
        ModItems.warpStones.sortedValues().map(DeferredItem::asResourceKey).forEach(warpStonesTag::add);
        builder(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard.asResourceKey(),
                ModItems.crumblingAttunedShard.asResourceKey(),
                ModItems.dormantShard.asResourceKey());
        builder(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard.asResourceKey());
        final var waystonesTag = builder(ModItemTags.WAYSTONES);
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asItem).map(it -> it.builtInRegistryHolder().key()).forEach(waystonesTag::add);

        final var sharestonesTag = builder(ModItemTags.SHARESTONES);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asItem).map(it -> it.builtInRegistryHolder().key()).forEach(sharestonesTag::add);

        final var portstonestag = builder(ModItemTags.PORTSTONES);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asItem).map(it -> it.builtInRegistryHolder().key()).forEach(portstonestag::add);

        builder(ModItemTags.WARP_MODIFIERS_SPEEDS_UP_WARP_PLATE).add(ItemIds.AMETHYST_SHARD);
        builder(ModItemTags.WARP_MODIFIERS_SLOWS_DOWN_WARP_PLATE).add(ItemIds.SLIME_BALL);
        builder(ModItemTags.WARP_MODIFIERS_PREFERS_ROUND_ROBIN).add(ItemIds.QUARTZ);
        builder(ModItemTags.WARP_MODIFIERS_PREFERS_SINGLE_USE).add(ItemIds.SPIDER_EYE);
        builder(ModItemTags.WARP_MODIFIERS_SETS_ON_FIRE).add(ItemIds.BLAZE_POWDER);
        builder(ModItemTags.WARP_MODIFIERS_POISONS).add(ItemIds.POISONOUS_POTATO);
        builder(ModItemTags.WARP_MODIFIERS_WITHERS).add(BlockItemIds.WITHER_ROSE);
        builder(ModItemTags.WARP_MODIFIERS_BLINDS).add(ItemIds.INK_SAC);
        builder(ModItemTags.WARP_MODIFIERS_CURES).add(ItemIds.MILK_BUCKET).add(BlockItemIds.HONEY_BLOCK);
        builder(ModItemTags.WARP_MODIFIERS_AMPLIFIES).add(ItemIds.DIAMOND);
        builder(ModItemTags.WARP_MODIFIERS_FEATHER_FALLS).add(ItemIds.FEATHER);
        builder(ModItemTags.WARP_MODIFIERS_RESISTS_FIRE).add(ItemIds.MAGMA_CREAM);

        builder(ModItemTags.WARP_MODIFIERS).add(
                ItemIds.BLAZE_POWDER,
                ItemIds.POISONOUS_POTATO,
                ItemIds.INK_SAC,
                ItemIds.MILK_BUCKET,
                ItemIds.DIAMOND,
                ItemIds.FEATHER,
                ItemIds.MAGMA_CREAM,
                ItemIds.QUARTZ,
                ItemIds.SPIDER_EYE
        ).add(BlockItemIds.HONEY_BLOCK, BlockItemIds.WITHER_ROSE);
    }
}
