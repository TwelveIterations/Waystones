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
        tag(ModItemTags.SCROLLS).add(ModItems.warpScroll.asItem(), ModItems.returnScroll.asItem(), ModItems.boundScroll.asItem(), ModItems.blankScroll.asItem());
        tag(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll.asItem());
        tag(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll.asItem());
        tag(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll.asItem());
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
    }
}
