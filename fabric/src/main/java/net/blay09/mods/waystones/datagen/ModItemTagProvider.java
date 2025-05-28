package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture, (item) -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/durability"))).add(ModItems.warpStone);
        tag(ModItemTags.SCROLLS).add(ModItems.warpScroll, ModItems.returnScroll, ModItems.boundScroll, ModItems.blankScroll);
        tag(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll);
        tag(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll);
        tag(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll);
        tag(ModItemTags.WARP_STONES).add(ModItems.warpStone);
        tag(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard,
                ModItems.crumblingAttunedShard,
                ModItems.dormantShard,
                ModItems.deepslateShard);
        tag(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard);
        tag(ModItemTags.WAYSTONES).add(ModBlocks.waystone.asItem(),
                ModBlocks.mossyWaystone.asItem(),
                ModBlocks.sandyWaystone.asItem(),
                ModBlocks.deepslateWaystone.asItem(),
                ModBlocks.blackstoneWaystone.asItem(),
                ModBlocks.endStoneWaystone.asItem());

        final var sharestonesTag = tag(ModItemTags.SHARESTONES);
        for (Block sharestone : ModBlocks.sharestones) {
            sharestonesTag.add(sharestone.asItem());
        }

        final var portstonestag = tag(ModItemTags.PORTSTONES);
        for (final var portstone : ModBlocks.portstones) {
            portstonestag.add(portstone.asItem());
        }
    }
}
