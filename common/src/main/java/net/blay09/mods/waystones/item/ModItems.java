package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static DeferredItem returnScroll;
    public static DeferredItem blankScroll;
    public static DeferredItem boundScroll;
    public static DeferredItem warpScroll;
    public static DeferredItem warpStone;
    public static DeferredItem dormantShard;
    public static DeferredItem attunedShard;
    public static DeferredItem deepslateShard;
    public static DeferredItem crumblingAttunedShard;

    public static void initialize(BalmItemRegistrar items) {
        returnScroll = items.register("return_scroll", ReturnScrollItem::new).asDeferredItem();
        blankScroll = items.register("blank_scroll", BlankScrollItem::new).asDeferredItem();
        boundScroll = items.register("bound_scroll", BoundScrollItem::new).asDeferredItem();
        warpScroll = items.register("warp_scroll", WarpScrollItem::new).asDeferredItem();
        warpStone = items.register("warp_stone", WarpStoneItem::new).asDeferredItem();
        dormantShard = items.register("dormant_shard", ShardItem::new).asDeferredItem();
        attunedShard = items.register("attuned_shard", AttunedShardItem::new).asDeferredItem();
        deepslateShard = items.register("deepslate_shard", ShardItem::new).asDeferredItem();
        crumblingAttunedShard = items.register("crumbling_attuned_shard", CrumblingAttunedShardItem::new).asDeferredItem();
    }

    public static void initialize(BalmCreativeModeTabRegistrar creativeModeTabs) {
        creativeModeTabs.register("waystones", (id, builder) ->
                builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                        .icon(() -> new ItemStack(ModBlocks.waystone))
                        .displayItems((displayParameters, output) -> {
                            output.accept(ModBlocks.waystone);
                            output.accept(ModBlocks.portstones.get(DyeColor.WHITE));
                            output.accept(ModBlocks.sharestones.get(DyeColor.RED));
                            output.accept(ModBlocks.warpPlate);
                            output.accept(ModItems.blankScroll);
                            output.accept(ModItems.returnScroll);
                            output.accept(ModItems.warpScroll);
                            output.accept(ModItems.warpStone);
                            output.accept(ModItems.dormantShard);
                            output.accept(ModBlocks.sandyWaystone);
                            output.accept(ModBlocks.mossyWaystone);
                            output.accept(ModBlocks.deepslateWaystone);
                            output.accept(ModBlocks.blackstoneWaystone);
                            output.accept(ModBlocks.endStoneWaystone);
                            ModBlocks.sharestones.forEach((color, block) -> {
                                if (color != DyeColor.RED) {
                                    output.accept(block);
                                }
                            });
                            ModBlocks.portstones.forEach((color, block) -> {
                                if (color != DyeColor.WHITE) {
                                    output.accept(block);
                                }
                            });
                        })
        );
    }

}
