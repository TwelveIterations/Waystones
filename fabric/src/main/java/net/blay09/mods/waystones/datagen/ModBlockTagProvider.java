package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture, (block) -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        final var mineablePickaxeTag = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("mineable/pickaxe"));
        final var mineableBuilder = tag(mineablePickaxeTag);
        mineableBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate);
        for (final var portstone : ModBlocks.portstones) {
            mineableBuilder.add(portstone);
        }
        for (final var sharestone : ModBlocks.sharestones) {
            mineableBuilder.add(sharestone);
        }

        final var isTeleportTargetBuilder = tag(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate);
        for (final var portstone : ModBlocks.portstones) {
            isTeleportTargetBuilder.add(portstone);
        }
        for (final var sharestone : ModBlocks.sharestones) {
            isTeleportTargetBuilder.add(sharestone);
        }

        tag(ModBlockTags.WAYSTONES).add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone);

        final var sharestonesBuilder = tag(ModBlockTags.SHARESTONES);
        for (final var sharestone : ModBlocks.sharestones) {
            sharestonesBuilder.add(sharestone);
        }

        final var portstonesBuilder = tag(ModBlockTags.PORTSTONES);
        for (final var portstone : ModBlocks.portstones) {
            portstonesBuilder.add(portstone);
        }
    }

}
