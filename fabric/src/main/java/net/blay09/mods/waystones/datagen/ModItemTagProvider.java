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

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture, (item) -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("enchantable/durability"))).add(ModItems.warpStone.asItem());
        tag(ModItemTags.SCROLLS).add(ModItems.warpScroll.asItem(), ModItems.returnScroll.asItem(), ModItems.boundScroll.asItem(), ModItems.blankScroll.asItem());
        tag(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll.asItem());
        tag(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll.asItem());
        tag(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll.asItem());
        tag(ModItemTags.WARP_STONES).add(ModItems.warpStone.asItem());
        tag(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard.asItem(),
                ModItems.crumblingAttunedShard.asItem(),
                ModItems.dormantShard.asItem());
        tag(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard.asItem());
        tag(ModItemTags.WAYSTONES).add(ModBlocks.waystone.asItem(),
                ModBlocks.mossyWaystone.asItem(),
                ModBlocks.sandyWaystone.asItem(),
                ModBlocks.deepslateWaystone.asItem(),
                ModBlocks.blackstoneWaystone.asItem(),
                ModBlocks.endStoneWaystone.asItem());

        final var sharestonesTag = tag(ModItemTags.SHARESTONES);
        ModBlocks.sharestones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it ->sharestonesTag.add(it.asItem()));

        final var portstonestag = tag(ModItemTags.PORTSTONES);
        ModBlocks.portstones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> portstonestag.add(it.asItem()));
    }
}
