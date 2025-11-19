package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.waystones.api.trait.IResetUseOnDamage;
import net.minecraft.world.entity.player.Player;

public class WarpDamageResetHandler {

    public static void register() {
        LivingEntityCallback.Damage.EVENT.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof Player && entity.getUseItem().getItem() instanceof IResetUseOnDamage) {
                entity.stopUsingItem();
            }
            return damageAmount;
        });
    }
}
