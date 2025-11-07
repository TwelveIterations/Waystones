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

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture, (item) -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/durability"))).add(ModItems.warpStone.asItem());
        tag(ModItemTags.SCROLLS).add(ModItems.warpScroll.asItem(), ModItems.returnScroll.asItem(), ModItems.boundScroll.asItem(), ModItems.blankScroll.asItem());
        tag(ModItemTags.WARP_SCROLLS).add(ModItems.warpScroll.asItem());
        tag(ModItemTags.RETURN_SCROLLS).add(ModItems.returnScroll.asItem());
        tag(ModItemTags.BOUND_SCROLLS).add(ModItems.boundScroll.asItem());
        tag(ModItemTags.WARP_STONES).add(ModItems.warpStone.asItem());
        tag(ModItemTags.WARP_SHARDS).add(ModItems.attunedShard.asItem(),
                ModItems.crumblingAttunedShard.asItem(),
                ModItems.dormantShard.asItem(),
                ModItems.deepslateShard.asItem());
        tag(ModItemTags.SINGLE_USE_WARP_SHARDS).add(ModItems.crumblingAttunedShard.asItem());
        tag(ModItemTags.WAYSTONES).add(ModBlocks.waystone.asItem(),
                ModBlocks.mossyWaystone.asItem(),
                ModBlocks.sandyWaystone.asItem(),
                ModBlocks.deepslateWaystone.asItem(),
                ModBlocks.blackstoneWaystone.asItem(),
                ModBlocks.endStoneWaystone.asItem());

        final var sharestonesTag = tag(ModItemTags.SHARESTONES);
        for (final var sharestone : ModBlocks.sharestones.values()) {
            sharestonesTag.add(sharestone.asItem());
        }

        final var portstonestag = tag(ModItemTags.PORTSTONES);
        for (final var portstone : ModBlocks.portstones.values()) {
            portstonestag.add(portstone.asItem());
        }
    }
}
