package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.waystones.api.IFOVOnUse;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WarpStoneFOVHandler {

    public static void onFOV(FovUpdateEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack activeItemStack = entity.getUseItem();
        if (isScrollItem(activeItemStack)) {
            float fov = entity.getUseItemRemainingTicks() / 32f * 2f;
            event.setFov((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1f, fov));
        }
    }

    private static boolean isScrollItem(ItemStack activeItemStack) {
        return !activeItemStack.isEmpty() && activeItemStack.getItem() instanceof IFOVOnUse;
    }

}
