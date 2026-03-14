package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.balm.world.item.DiscriminatedItems;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ModItems {
    public static DeferredItem returnScroll;
    public static DeferredItem blankScroll;
    public static DeferredItem boundScroll;
    public static DeferredItem warpScroll;
    public static DiscriminatedItems<SharestoneType> warpStones;
    public static DeferredItem dormantShard;
    public static DeferredItem attunedShard;
    public static DeferredItem crumblingAttunedShard;

    public static void initialize(BalmItemRegistrar items) {
        returnScroll = items.register("return_scroll", ReturnScrollItem::new).asDeferredItem();
        blankScroll = items.register("blank_scroll", BlankScrollItem::new).asDeferredItem();
        boundScroll = items.register("bound_scroll", BoundScrollItem::new).asDeferredItem();
        warpScroll = items.register("warp_scroll", WarpScrollItem::new).asDeferredItem();
        final var sharestoneTypes = Set.of(SharestoneType.values());
        final var sharestoneTypesWithNull = new HashSet<@Nullable SharestoneType>(sharestoneTypes);
        sharestoneTypesWithNull.add(null);
        warpStones = items.registerDiscriminated(sharestoneTypesWithNull, type -> DiscriminatedItems.prefix(type, "warp_stone"), WarpStoneItem::new, it -> it).asDiscriminatedItems();
        dormantShard = items.register("dormant_shard", ShardItem::new).asDeferredItem();
        attunedShard = items.register("attuned_shard", AttunedShardItem::new).asDeferredItem();
        crumblingAttunedShard = items.register("crumbling_attuned_shard", CrumblingAttunedShardItem::new).asDeferredItem();
    }

    public static void initialize(BalmCreativeModeTabRegistrar creativeModeTabs) {
        creativeModeTabs.register("waystones", (id, builder) ->
                builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                        .icon(() -> new ItemStack(ModBlocks.waystones.get(WaystoneType.ANDESITE)))
                        .displayItems((_, output) -> {
                            output.accept(ModBlocks.waystones.get(WaystoneType.ANDESITE));
                            output.accept(ModBlocks.portstones.get(null));
                            output.accept(ModBlocks.sharestones.get(SharestoneType.COPPER));
                            output.accept(ModBlocks.warpPlate);
                            output.accept(ModItems.blankScroll);
                            output.accept(ModItems.returnScroll);
                            output.accept(ModItems.warpScroll);
                            output.accept(ModItems.warpStones.get(null));
                            output.accept(ModItems.dormantShard);
                            ModBlocks.waystones.forEach((type, block) -> {
                                if (type != WaystoneType.ANDESITE) {
                                    output.accept(block);
                                }
                            });
                            ModBlocks.sharestones.forEach((type, block) -> {
                                if (type != SharestoneType.COPPER) {
                                    output.accept(block);
                                }
                            });
                            ModBlocks.portstones.forEach((type, block) -> {
                                if (type != null) {
                                    output.accept(block);
                                }
                            });
                            ModItems.warpStones.forEach((type, item) -> {
                                if (type != null) {
                                    output.accept(item);
                                }
                            });
                        })
        );
    }

}
