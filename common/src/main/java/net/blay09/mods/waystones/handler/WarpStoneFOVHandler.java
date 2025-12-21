package net.blay09.mods.waystones.handler;

import net.blay09.mods.waystones.api.trait.IFOVOnUse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WarpStoneFOVHandler {

    public static float onFOV(LivingEntity entity, float fov) {
        ItemStack activeItemStack = entity.getUseItem();
        if (isScrollItem(activeItemStack)) {
            return entity.getUseItemRemainingTicks() / 32f * 2f;
        }

        return fov;
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
