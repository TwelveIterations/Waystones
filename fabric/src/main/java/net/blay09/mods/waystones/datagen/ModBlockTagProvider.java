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

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        final var relocationNotSupported = getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "relocation_not_supported")));
        relocationNotSupported.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone,
                ModBlocks.warpPlate);
        for (final var portstone : ModBlocks.portstones) {
            relocationNotSupported.add(portstone);
        }
        for (final var sharestone : ModBlocks.sharestones) {
            relocationNotSupported.add(sharestone);
        }

        final var mineablePickaxeTag = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("mineable/pickaxe"));
        final var mineableBuilder = getOrCreateTagBuilder(mineablePickaxeTag);
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

        final var isTeleportTargetBuilder = getOrCreateTagBuilder(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.warpPortal,
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

        getOrCreateTagBuilder(ModBlockTags.WAYSTONES).add(ModBlocks.waystone,
                ModBlocks.sandyWaystone,
                ModBlocks.mossyWaystone,
                ModBlocks.deepslateWaystone,
                ModBlocks.blackstoneWaystone,
                ModBlocks.endStoneWaystone);

        final var sharestonesBuilder = getOrCreateTagBuilder(ModBlockTags.SHARESTONES);
        for (final var sharestone : ModBlocks.sharestones) {
            sharestonesBuilder.add(sharestone);
        }

        final var portstonesBuilder = getOrCreateTagBuilder(ModBlockTags.PORTSTONES);
        for (final var portstone : ModBlocks.portstones) {
            portstonesBuilder.add(portstone);
        }
    }

}
