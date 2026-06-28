package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.balm.world.level.block.DiscriminatedBlocks;
import net.blay09.mods.waystones.api.PortstoneTypes;
import net.blay09.mods.waystones.api.SharestoneTypes;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.component.DescriptionComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.item.PortstoneBlockItem;
import net.blay09.mods.waystones.item.SharestoneBlockItem;
import net.blay09.mods.waystones.item.WaystoneBlockItem;
import net.blay09.mods.waystones.migration.MigrationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;

import java.util.stream.Collectors;

public class ModBlocks {

    public static DiscriminatedBlocks<BuiltinWaystoneType> waystones;
    public static DeferredBlock warpPlate;
    public static DeferredBlock warpPortal;
    public static DiscriminatedBlocks<BuiltinPortstoneType> portstones;
    public static DiscriminatedBlocks<BuiltinSharestoneType> sharestones;

    public static void initialize(BalmBlockRegistrar blocks) {
        final var waystoneTypes = WaystoneTypes.builtinValues().collect(Collectors.toSet());
        waystones = blocks.registerDiscriminated(waystoneTypes, DiscriminatedBlocks.suffixWith("waystone"), WaystoneBlock::new, (type, properties) -> properties.sound(type.soundType()).strength(5f, 2000f))
                .withItems(WaystoneBlockItem::new)
                .asDiscriminatedBlocks();
        warpPlate = blocks.register("warp_plate", WarpPlateBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withDefaultItem().asDeferredBlock();
        warpPortal = blocks.register("warp_portal", WarpPortalBlock::new, it -> it.noLootTable().lightLevel(_ -> 10).strength(-1f, 3600000f)).asDeferredBlock();

        final var sharestoneTypes = SharestoneTypes.builtinValues().collect(Collectors.toSet());
        final var portstoneTypes = PortstoneTypes.builtinValues().collect(Collectors.toSet());
        portstones = blocks.registerDiscriminated(portstoneTypes, type -> type != PortstoneTypes.UNSCOPED ? type + "_portstone" : "portstone", PortstoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(PortstoneBlockItem::new, it -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones.portstone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();

        sharestones = blocks.registerDiscriminated(sharestoneTypes, DiscriminatedBlocks.suffixWith("sharestone"), SharestoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(SharestoneBlockItem::new, (color, it) -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones." + color + "_sharestone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();

        MigrationUtils.migrateBlocks(blocks);
    }

}
