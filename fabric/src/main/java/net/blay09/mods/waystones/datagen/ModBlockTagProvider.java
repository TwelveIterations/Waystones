package net.blay09.mods.waystones.datagen;

import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture, (block) -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        final var mineablePickaxeTag = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("mineable/pickaxe"));
        final var mineableBuilder = tag(mineablePickaxeTag);
        mineableBuilder.add(ModBlocks.waystone.asBlock(),
                ModBlocks.sandyWaystone.asBlock(),
                ModBlocks.mossyWaystone.asBlock(),
                ModBlocks.deepslateWaystone.asBlock(),
                ModBlocks.blackstoneWaystone.asBlock(),
                ModBlocks.endStoneWaystone.asBlock(),
                ModBlocks.warpPlate.asBlock());
        ModBlocks.portstones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> mineableBuilder.add(it.asBlock()));
        ModBlocks.sharestones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> mineableBuilder.add(it.asBlock()));

        final var isTeleportTargetBuilder = tag(ModBlockTags.IS_TELEPORT_TARGET);
        isTeleportTargetBuilder.add(ModBlocks.waystone.asBlock(),
                ModBlocks.sandyWaystone.asBlock(),
                ModBlocks.mossyWaystone.asBlock(),
                ModBlocks.deepslateWaystone.asBlock(),
                ModBlocks.blackstoneWaystone.asBlock(),
                ModBlocks.endStoneWaystone.asBlock(),
                ModBlocks.warpPlate.asBlock());
        ModBlocks.portstones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> isTeleportTargetBuilder.add(it.asBlock()));
        ModBlocks.sharestones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> isTeleportTargetBuilder.add(it.asBlock()));

        tag(ModBlockTags.WAYSTONES).add(ModBlocks.waystone.asBlock(),
                ModBlocks.sandyWaystone.asBlock(),
                ModBlocks.mossyWaystone.asBlock(),
                ModBlocks.deepslateWaystone.asBlock(),
                ModBlocks.blackstoneWaystone.asBlock(),
                ModBlocks.endStoneWaystone.asBlock());

        final var sharestonesBuilder = tag(ModBlockTags.SHARESTONES);
        ModBlocks.sharestones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> sharestonesBuilder.add(it.asBlock()));

        final var portstonesBuilder = tag(ModBlockTags.PORTSTONES);
        ModBlocks.portstones.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder()))).map(Map.Entry::getValue).forEach(it -> portstonesBuilder.add(it.asBlock()));
    }

}
