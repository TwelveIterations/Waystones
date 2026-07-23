package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WaystoneModifierSlot extends Slot {
    public WaystoneModifierSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        if (itemStack.is(ModItems.dormantShard.asItem())) {
            return 1;
        }

        return super.getMaxStackSize(itemStack);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        if (itemStack.is(ModItems.dormantShard.asItem())) {
            return getContainerSlot() == 0;
        }

        return super.mayPlace(itemStack);
    }
}
