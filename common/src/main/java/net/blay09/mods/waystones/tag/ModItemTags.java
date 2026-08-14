package net.blay09.mods.waystones.tag;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> SCROLLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "scrolls"));
    public static final TagKey<Item> BOUND_SCROLLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "bound_scrolls"));
    public static final TagKey<Item> RETURN_SCROLLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "return_scrolls"));
    public static final TagKey<Item> WARP_SCROLLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_scrolls"));
    public static final TagKey<Item> PORTAL_SCROLLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "portal_scrolls"));
    public static final TagKey<Item> WARP_STONES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_stones"));
    public static final TagKey<Item> WARP_SHARDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_shards"));
    public static final TagKey<Item> SINGLE_USE_WARP_SHARDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "single_use_warp_shards"));
    public static final TagKey<Item> WAYSTONES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "waystones"));
    public static final TagKey<Item> SHARESTONES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "sharestones"));
    public static final TagKey<Item> PORTSTONES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "portstones"));
    public static final TagKey<Item> WARP_MODIFIERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers"));
    public static final TagKey<Item> WARP_MODIFIERS_SPEEDS_UP_WARP_PLATE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/speeds_up_warp_plate"));
    public static final TagKey<Item> WARP_MODIFIERS_SLOWS_DOWN_WARP_PLATE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/slows_down_warp_plate"));
    public static final TagKey<Item> WARP_MODIFIERS_PREFERS_ROUND_ROBIN = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/prefers_round_robin"));
    public static final TagKey<Item> WARP_MODIFIERS_PREFERS_SINGLE_USE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/prefers_single_use"));
    public static final TagKey<Item> WARP_MODIFIERS_REDSTONE_SENSITIVE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/redstone_sensitive"));
    public static final TagKey<Item> WARP_MODIFIERS_SETS_ON_FIRE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/sets_on_fire"));
    public static final TagKey<Item> WARP_MODIFIERS_POISONS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/poisons"));
    public static final TagKey<Item> WARP_MODIFIERS_WITHERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/withers"));
    public static final TagKey<Item> WARP_MODIFIERS_BLINDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/blinds"));
    public static final TagKey<Item> WARP_MODIFIERS_CURES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/cures"));
    public static final TagKey<Item> WARP_MODIFIERS_AMPLIFIES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/amplifies"));
    public static final TagKey<Item> WARP_MODIFIERS_FEATHER_FALLS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/feather_falls"));
    public static final TagKey<Item> WARP_MODIFIERS_RESISTS_FIRE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "warp_modifiers/resists_fire"));
}
