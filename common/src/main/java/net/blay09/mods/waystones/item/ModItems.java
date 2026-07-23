package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.balm.world.item.DiscriminatedItems;
import net.blay09.mods.waystones.api.PortstoneTypes;
import net.blay09.mods.waystones.api.SharestoneTypes;
import net.blay09.mods.waystones.api.WarpStoneTypes;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.BuiltinWarpStoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.component.DescriptionComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.blay09.mods.waystones.migration.MigrationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ModItems {
    public static DeferredItem returnScroll;
    public static DeferredItem blankScroll;
    public static DeferredItem boundScroll;
    public static DeferredItem warpScroll;
    public static DeferredItem portalScroll;
    public static DiscriminatedItems<BuiltinWarpStoneType> warpStones;
    public static DeferredItem dormantShard;
    public static DeferredItem attunedShard;
    public static DeferredItem crumblingAttunedShard;
    public static DeferredItem twinboundFeather;
    public static DeferredItem epitaph;

    public static void initialize(BalmItemRegistrar items) {
        returnScroll = items.register("return_scroll", ReturnScrollItem::new).asDeferredItem();
        blankScroll = items.register("blank_scroll", BlankScrollItem::new).asDeferredItem();
        boundScroll = items.register("bound_scroll", BoundScrollItem::new).asDeferredItem();
        warpScroll = items.register("warp_scroll", WarpScrollItem::new).asDeferredItem();
        portalScroll = items.register("portal_scroll", PortalScrollItem::new).asDeferredItem();
        final var warpStoneTypes = WarpStoneTypes.builtinValues().collect(Collectors.toCollection(HashSet::new));
        warpStones = items.registerDiscriminated(warpStoneTypes, type -> type != WarpStoneTypes.UNSCOPED ? type + "_warp_stone" : "warp_stone", WarpStoneItem::new, (type, it) -> {
            if (type == WarpStoneTypes.UNSCOPED) {
                return it;
            }

            return it.component(ModComponents.description.value(), new DescriptionComponent(Component.translatable("tooltip.waystones." + type + "_sharestone").withStyle(ChatFormatting.GRAY)));
        }).asDiscriminatedItems();
        dormantShard = items.register("dormant_shard", ShardItem::new).asDeferredItem();
        attunedShard = items.register("attuned_shard", AttunedShardItem::new).asDeferredItem();
        crumblingAttunedShard = items.register("crumbling_attuned_shard", CrumblingAttunedShardItem::new).asDeferredItem();
        twinboundFeather = items.register("twinbound_feather", TwinboundFeatherItem::new).asDeferredItem();
        epitaph = items.register("epitaph", Item::new, it -> it.stacksTo(1)).asDeferredItem();

        MigrationUtils.migrateItems(items);
    }

    public static void initialize(BalmCreativeModeTabRegistrar creativeModeTabs) {
        creativeModeTabs.register("waystones", (id, builder) ->
                builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                        .icon(() -> new ItemStack(ModBlocks.waystones.get(WaystoneTypes.ANDESITE)))
                        .displayItems((_, output) -> {
                            output.accept(ModBlocks.waystones.get(WaystoneTypes.ANDESITE));
                            output.accept(ModBlocks.portstones.get(PortstoneTypes.UNSCOPED));
                            output.accept(ModBlocks.sharestones.get(SharestoneTypes.COPPER));
                            output.accept(ModBlocks.warpPlate);
                            output.accept(ModItems.blankScroll);
                            output.accept(ModItems.returnScroll);
                            output.accept(ModItems.warpScroll);
                            output.accept(ModItems.portalScroll);
                            output.accept(ModItems.warpStones.get(WarpStoneTypes.UNSCOPED));
                            output.accept(ModItems.dormantShard);
                            output.accept(ModItems.twinboundFeather);
                            output.accept(ModItems.epitaph);
                            ModBlocks.waystones.forEach((type, block) -> {
                                if (type != WaystoneTypes.ANDESITE) {
                                    output.accept(block);
                                }
                            });
                            ModBlocks.sharestones.forEach((type, block) -> {
                                if (type != SharestoneTypes.COPPER && type != SharestoneTypes.RUINED) {
                                    output.accept(block);
                                }
                            });
                            ModBlocks.portstones.forEach((type, block) -> {
                                if (type != PortstoneTypes.UNSCOPED) {
                                    output.accept(block);
                                }
                            });
                            ModItems.warpStones.forEach((type, item) -> {
                                if (type != WarpStoneTypes.UNSCOPED) {
                                    output.accept(item);
                                }
                            });
                        })
        );
    }

}
