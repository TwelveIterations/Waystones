package net.blay09.mods.waystones.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class WaystoneRecipeInput implements RecipeInput {
    private final NonNullList<ItemStack> items;

    private WaystoneRecipeInput(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static WaystoneRecipeInput of(Container container) {
        final var items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            items.set(i, container.getItem(i));
        }
        return new WaystoneRecipeInput(items);
    }

    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }
}
