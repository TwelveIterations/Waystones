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
            float newFov = entity.getUseItemRemainingTicks() / 32f * 2f;
            return (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1f, newFov);
        }
        return fov;
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
