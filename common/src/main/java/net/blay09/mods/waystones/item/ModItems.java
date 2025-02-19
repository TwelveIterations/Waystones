package net.blay09.mods.waystones.item;


import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static Item returnScroll;
    public static Item boundScroll;
    public static Item warpScroll;
    public static Item warpStone;
    public static Item warpDust;
    public static Item attunedShard;
    public static Item crumblingAttunedShard;

    public static void initialize(BalmItems items) {
        items.registerItem(() -> returnScroll = new ReturnScrollItem(items.itemProperties()), id("return_scroll"));
        items.registerItem(() -> boundScroll = new BoundScrollItem(items.itemProperties()), id("bound_scroll"));
        items.registerItem(() -> warpScroll = new WarpScrollItem(items.itemProperties()), id("warp_scroll"));
        items.registerItem(() -> warpStone = new WarpStoneItem(items.itemProperties()), id("warp_stone"));
        items.registerItem(() -> warpDust = new WarpDustItem(items.itemProperties()), id("warp_dust"));
        items.registerItem(() -> attunedShard = new AttunedShardItem(items.itemProperties()), id("attuned_shard"), null);
        items.registerItem(() -> crumblingAttunedShard = new CrumblingAttunedShardItem(items.itemProperties()), id("crumbling_attuned_shard"), null);

        creativeModeTab = items.registerCreativeModeTab(id("waystones"), () -> new ItemStack(ModBlocks.waystone));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }
    
}
