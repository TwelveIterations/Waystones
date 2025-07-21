package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class WaystonesBlockTagProvider extends FabricTagProvider<Block> {
    public WaystonesBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        TagKey<Block> mineablePickaxeTag = TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "mineable/pickaxe"));
        FabricTagProvider<Block>.FabricTagBuilder mineableBuilder = getOrCreateTagBuilder(mineablePickaxeTag);
        mineableBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate,
                ModBlocks.portstone,
                ModBlocks.sharestone);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            mineableBuilder.add(scopedSharestone);
        }

        FabricTagProvider<Block>.FabricTagBuilder isTeleportTargetBuilder = getOrCreateTagBuilder(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate,
                ModBlocks.portstone,
                ModBlocks.sharestone);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            isTeleportTargetBuilder.add(scopedSharestone);
        }

        getOrCreateTagBuilder(ModBlockTags.WAYSTONES).add(ModBlocks.waystone, ModBlocks.sandyWaystone, ModBlocks.mossyWaystone);

        FabricTagProvider<Block>.FabricTagBuilder sharestonesBuilder = getOrCreateTagBuilder(ModBlockTags.SHARESTONES);
        FabricTagProvider<Block>.FabricTagBuilder dyedSharestonesTag = getOrCreateTagBuilder(ModBlockTags.DYED_SHARESTONES);
        sharestonesBuilder.add(ModBlocks.sharestone);
        for (Block scopedSharestone : ModBlocks.scopedSharestones) {
            sharestonesBuilder.add(scopedSharestone);
            dyedSharestonesTag.add(scopedSharestone);
        }
    }

}
