package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        final var relocationNotSupported = builder(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "relocation_not_supported")));
        relocationNotSupported.add(ModBlocks.fleetingMemorial.asResourceKey());
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asResourceKey).forEach(relocationNotSupported::add);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asResourceKey).forEach(relocationNotSupported::add);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asResourceKey).forEach(relocationNotSupported::add);

        final var mineableBuilder = builder(BlockTags.MINEABLE_WITH_PICKAXE);
        mineableBuilder.add(ModBlocks.warpPlate.asResourceKey());
        mineableBuilder.add(ModBlocks.fleetingMemorial.asResourceKey());
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asResourceKey).forEach(mineableBuilder::add);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asResourceKey).forEach(mineableBuilder::add);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asResourceKey).forEach(mineableBuilder::add);

        final var isTeleportTargetBuilder = builder(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.warpPlate.asResourceKey());
        isTeleportTargetBuilder.add(ModBlocks.warpPortal.asResourceKey());
        isTeleportTargetBuilder.add(ModBlocks.fleetingMemorial.asResourceKey());
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asResourceKey).forEach(isTeleportTargetBuilder::add);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asResourceKey).forEach(isTeleportTargetBuilder::add);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asResourceKey).forEach(isTeleportTargetBuilder::add);

        final var waystonesTagBuilder = builder(ModBlockTags.WAYSTONES);
        ModBlocks.waystones.sortedValues().map(DeferredBlock::asResourceKey).forEach(waystonesTagBuilder::add);

        final var sharestonesBuilder = builder(ModBlockTags.SHARESTONES);
        ModBlocks.sharestones.sortedValues().map(DeferredBlock::asResourceKey).forEach(sharestonesBuilder::add);

        final var portstonesBuilder = builder(ModBlockTags.PORTSTONES);
        ModBlocks.portstones.sortedValues().map(DeferredBlock::asResourceKey).forEach(portstonesBuilder::add);
    }

}
