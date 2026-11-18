package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.WaystoneType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.sounds.BlockSoundSet;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class BuiltinWaystoneType extends WaystoneType {

    private final String name;
    private final ResourceKey<BlockSoundSet> soundSet;
    private final Block particleBlock;
    private final Supplier<Ingredient> ingredientSupplier;

    public BuiltinWaystoneType(String name, int runeColor, ResourceKey<BlockSoundSet> soundSet, Block particleBlock, Supplier<Ingredient> ingredientSupplier, Supplier<Block> blockSupplier) {
        super(id(name + "_waystone"), runeColor, blockSupplier);
        this.name = name;
        this.soundSet = soundSet;
        this.particleBlock = particleBlock;
        this.ingredientSupplier = ingredientSupplier;
    }

    public Ingredient ingredient() {
        return ingredientSupplier.get();
    }

    @Override
    public String toString() {
        return name;
    }

    public Block particleBlock() {
        return particleBlock;
    }

    public ResourceKey<BlockSoundSet> soundSet() {
        return soundSet;
    }
}
