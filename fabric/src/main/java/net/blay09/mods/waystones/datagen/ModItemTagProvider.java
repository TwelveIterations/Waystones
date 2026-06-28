package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture, (item) -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        final var enchantableDurabilityTag = tag(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("enchantable/durability")));
        ModItems.warpStones.sortedValues().map(ItemLike::asItem).forEach(enchantableDurabilityTag::add);
        tag(ModItemTags.SCROLLS).add(ModItems.warpScroll.asItem(), ModItems.portalScroll.asItem(), ModItems.returnScroll.asItem(), ModItems.boundScroll.asItem(), ModItems.blankScroll.asItem());
        tag(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll.asItem());
        tag(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll.asItem());
        tag(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll.asItem());
        tag(ModItemTags.PORTAL_SCROLLS).add(ModItems.portalScroll.asItem());
        final var warpStonesTag = tag(ModItemTags.WARP_STONES);
        ModItems.warpStones.sortedValues().map(ItemLike::asItem).forEach(warpStonesTag::add);
        tag(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard.asItem(),
                ModItems.crumblingAttunedShard.asItem(),
                ModItems.dormantShard.asItem());
        tag(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard.asItem());
        final var waystonesTag = tag(ModItemTags.WAYSTONES);
        ModBlocks.waystones.sortedValues().map(ItemLike::asItem).forEach(waystonesTag::add);

        final var sharestonesTag = tag(ModItemTags.SHARESTONES);
        ModBlocks.sharestones.sortedValues().map(ItemLike::asItem).forEach(sharestonesTag::add);

        final var portstonestag = tag(ModItemTags.PORTSTONES);
        ModBlocks.portstones.sortedValues().map(ItemLike::asItem).forEach(portstonestag::add);

        tag(ModItemTags.WARP_MODIFIERS_SPEEDS_UP_WARP_PLATE).add(Items.AMETHYST_SHARD);
        tag(ModItemTags.WARP_MODIFIERS_SLOWS_DOWN_WARP_PLATE).add(Items.SLIME_BALL);
        tag(ModItemTags.WARP_MODIFIERS_PREFERS_ROUND_ROBIN).add(Items.QUARTZ);
        tag(ModItemTags.WARP_MODIFIERS_PREFERS_SINGLE_USE).add(Items.SPIDER_EYE);
        tag(ModItemTags.WARP_MODIFIERS_SETS_ON_FIRE).add(Items.BLAZE_POWDER);
        tag(ModItemTags.WARP_MODIFIERS_POISONS).add(Items.POISONOUS_POTATO);
        tag(ModItemTags.WARP_MODIFIERS_WITHERS).add(Items.WITHER_ROSE);
        tag(ModItemTags.WARP_MODIFIERS_BLINDS).add(Items.INK_SAC);
        tag(ModItemTags.WARP_MODIFIERS_CURES).add(Items.MILK_BUCKET, Items.HONEY_BLOCK);
        tag(ModItemTags.WARP_MODIFIERS_AMPLIFIES).add(Items.DIAMOND);
        tag(ModItemTags.WARP_MODIFIERS_FEATHER_FALLS).add(Items.FEATHER);
        tag(ModItemTags.WARP_MODIFIERS_RESISTS_FIRE).add(Items.MAGMA_CREAM);

        tag(ModItemTags.WARP_MODIFIERS).add(
                Items.BLAZE_POWDER,
                Items.POISONOUS_POTATO,
                Items.INK_SAC,
                Items.MILK_BUCKET,
                Items.HONEY_BLOCK,
                Items.DIAMOND,
                Items.FEATHER,
                Items.MAGMA_CREAM,
                Items.WITHER_ROSE,
                Items.QUARTZ,
                Items.SPIDER_EYE
        );
    }
}
