package net.blay09.mods.waystones.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.balm.world.level.block.DiscriminatedBlocks;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.component.DescriptionComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.item.PortstoneBlockItem;
import net.blay09.mods.waystones.item.SharestoneBlockItem;
import net.blay09.mods.waystones.item.WaystoneBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {

    public static DeferredBlock waystone;
    public static DeferredBlock mossyWaystone;
    public static DeferredBlock sandyWaystone;
    public static DeferredBlock deepslateWaystone;
    public static DeferredBlock blackstoneWaystone;
    public static DeferredBlock endStoneWaystone;
    public static DeferredBlock warpPlate;
    public static DiscriminatedBlocks<SharestoneType> portstones;
    public static DiscriminatedBlocks<SharestoneType> sharestones;

    public static void initialize(BalmBlockRegistrar blocks) {
        waystone = blocks.register("waystone", WaystoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        mossyWaystone = blocks.register("mossy_waystone", WaystoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        sandyWaystone = blocks.register("sandy_waystone", WaystoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        deepslateWaystone = blocks.register("deepslate_waystone", WaystoneBlock::new, it -> it.sound(SoundType.DEEPSLATE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        blackstoneWaystone = blocks.register("blackstone_waystone", WaystoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        endStoneWaystone = blocks.register("end_stone_waystone", WaystoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withItem(WaystoneBlockItem::new).asDeferredBlock();
        warpPlate = blocks.register("warp_plate", WarpPlateBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f)).withDefaultItem().asDeferredBlock();

        final var sharestoneTypes = Set.of(SharestoneType.values());
        final var sharestoneTypesWithNull = new HashSet<@Nullable SharestoneType>(sharestoneTypes);
        sharestoneTypesWithNull.add(null);
        portstones = blocks.registerDiscriminated(sharestoneTypesWithNull, type -> DiscriminatedBlocks.prefix(type, "portstone"), PortstoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(PortstoneBlockItem::new, it -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones.portstone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();

        sharestones = blocks.registerDiscriminated(sharestoneTypes, type -> DiscriminatedBlocks.prefix(type, "sharestone"), SharestoneBlock::new, it -> it.sound(SoundType.STONE).strength(5f, 2000f))
                .withItems(SharestoneBlockItem::new, (color, it) -> it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones." + color + "_sharestone").withStyle(ChatFormatting.GRAY))))
                .asDiscriminatedBlocks();
    }

}
