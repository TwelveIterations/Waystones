package net.blay09.mods.waystones.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WarpDustItem extends Item {

    public WarpDustItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }

}
