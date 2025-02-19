package net.blay09.mods.waystones.tag;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@Deprecated
public class ModTags {

    public static final TagKey<Item> BOUND_SCROLLS = TagKey.create(Registries.ITEM, new ResourceLocation(Waystones.MOD_ID, "bound_scrolls"));
    public static final TagKey<Item> RETURN_SCROLLS = TagKey.create(Registries.ITEM, new ResourceLocation(Waystones.MOD_ID, "return_scrolls"));
    public static final TagKey<Item> WARP_SCROLLS = TagKey.create(Registries.ITEM, new ResourceLocation(Waystones.MOD_ID, "warp_scrolls"));
    public static final TagKey<Item> WARP_STONES = TagKey.create(Registries.ITEM, new ResourceLocation(Waystones.MOD_ID, "warp_stones"));

    public static final TagKey<Block> IS_TELEPORT_TARGET = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "is_teleport_target"));
    public static final TagKey<Block> WAYSTONES = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "waystones"));
    public static final TagKey<Block> SHARESTONES = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "sharestones"));

}
