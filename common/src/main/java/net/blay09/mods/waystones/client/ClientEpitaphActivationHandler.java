package net.blay09.mods.waystones.client;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class ClientEpitaphActivationHandler {

    public static void playEffects() {
        final var minecraft = Minecraft.getInstance();
        final var player = minecraft.player;
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.TOTEM_USE, 1f, 1f));
        if (player != null) {
            player.displayItemActivation(new ItemStack(ModItems.epitaph.asItem()));
        }
    }

}
