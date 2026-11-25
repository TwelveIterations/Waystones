package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class ClientEpitaphActivationHandler {

    public static void playEffects() {
        final var minecraft = Minecraft.getInstance();
        final var player = minecraft.player;
        if (player != null) {
            if (!player.isSilent()) {
                player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, player.getSoundSource(), 1f, 1f, false);
            }
            player.displayItemActivation(new ItemStack(ModItems.epitaph.asItem()));
        }
    }

}
