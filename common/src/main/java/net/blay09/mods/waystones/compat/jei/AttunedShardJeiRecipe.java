package net.blay09.mods.waystones.compat.jei;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class AttunedShardJeiRecipe {
    private final List<ItemStack> inputs = new ArrayList<>();
    private final ItemStack output;

    public AttunedShardJeiRecipe() {
        inputs.add(new ItemStack(Items.FLINT));
        inputs.add(new ItemStack(ModItems.warpDust));
        inputs.add(new ItemStack(ModItems.warpDust));
        inputs.add(new ItemStack(ModItems.warpDust));
        inputs.add(new ItemStack(ModItems.warpDust));
        output = new ItemStack(ModItems.attunedShard);
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }
}
