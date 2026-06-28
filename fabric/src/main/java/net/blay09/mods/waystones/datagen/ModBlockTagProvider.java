package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture, (block) -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        final var relocationNotSupported = tag(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "relocation_not_supported")));
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asBlock).forEach(relocationNotSupported::add);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asBlock).forEach(relocationNotSupported::add);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asBlock).forEach(relocationNotSupported::add);

        final var mineableBuilder = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        mineableBuilder.add(ModBlocks.warpPlate.asBlock());
        ModBlocks.waystones.sortedValues().forEach(it -> mineableBuilder.add(it.asBlock()));
        ModBlocks.portstones.sortedValues().forEach(it -> mineableBuilder.add(it.asBlock()));
        ModBlocks.sharestones.sortedValues().forEach(it -> mineableBuilder.add(it.asBlock()));

        final var isTeleportTargetBuilder = tag(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.warpPlate.asBlock());
        isTeleportTargetBuilder.add(ModBlocks.warpPortal.asBlock());
        ModBlocks.waystones.sortedValues().forEach(it -> isTeleportTargetBuilder.add(it.asBlock()));
        ModBlocks.portstones.sortedValues().forEach(it -> isTeleportTargetBuilder.add(it.asBlock()));
        ModBlocks.sharestones.sortedValues().forEach(it -> isTeleportTargetBuilder.add(it.asBlock()));

        final var waystonesTagBuilder = tag(ModBlockTags.WAYSTONES);
        ModBlocks.waystones.sortedValues().forEach(it -> waystonesTagBuilder.add(it.asBlock()));

        final var sharestonesBuilder = tag(ModBlockTags.SHARESTONES);
        ModBlocks.sharestones.sortedValues().forEach(it -> sharestonesBuilder.add(it.asBlock()));

        final var portstonesBuilder = tag(ModBlockTags.PORTSTONES);
        ModBlocks.portstones.sortedValues().forEach(it -> portstonesBuilder.add(it.asBlock()));
    }

}
