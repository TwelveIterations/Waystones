package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.balm.world.level.block.DiscriminatedBlocks;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.component.DescriptionComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.item.PortstoneBlockItem;
import net.blay09.mods.waystones.item.SharestoneBlockItem;
import net.blay09.mods.waystones.item.WaystoneBlockItem;
import net.blay09.mods.waystones.migration.MigrationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {

    public static DiscriminatedBlocks<WaystoneType> waystones;
    public static DeferredBlock warpPlate;
    public static DiscriminatedBlocks<SharestoneType> portstones;
    public static DiscriminatedBlocks<SharestoneType> sharestones;

    public static void initialize(BalmBlockRegistrar blocks) {
        final var waystoneTypes = Set.of(WaystoneType.values());
        waystones = blocks.registerDiscriminated(waystoneTypes, type -> DiscriminatedBlocks.prefix(type != WaystoneType.ANDESITE ? type : null, "waystone"), WaystoneBlock::new, (type, properties) -> properties.sound(type.getSoundType()).strength(5f, 2000f))
                .withItems(WaystoneBlockItem::new)
                .asDiscriminatedBlocks();
        warpPlate = blocks.register("warp_plate", WarpPlateBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withDefaultItem().asDeferredBlock();

        final var sharestoneTypes = Set.of(SharestoneType.values());
        final var portstoneTypes = new HashSet<@Nullable SharestoneType>(sharestoneTypes);
        portstoneTypes.remove(SharestoneType.RUINED);
        portstoneTypes.add(null);
        portstones = blocks.registerDiscriminated(portstoneTypes, type -> DiscriminatedBlocks.prefix(type, "portstone"), PortstoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(PortstoneBlockItem::new, it -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones.portstone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();

        sharestones = blocks.registerDiscriminated(sharestoneTypes, type -> DiscriminatedBlocks.prefix(type, "sharestone"), SharestoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(SharestoneBlockItem::new, (color, it) -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones." + color + "_sharestone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();

        MigrationUtils.migrateBlocks(blocks);
    }

}
