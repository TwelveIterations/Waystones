package net.blay09.mods.waystones.tag;

import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> IS_TELEPORT_TARGET = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "is_teleport_target"));
    public static final TagKey<Block> WAYSTONES = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "waystones"));
    public static final TagKey<Block> SHARESTONES = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "sharestones"));
    public static final TagKey<Block> DYED_SHARESTONES = TagKey.create(Registries.BLOCK, new ResourceLocation(Waystones.MOD_ID, "dyed_sharestones"));
}
