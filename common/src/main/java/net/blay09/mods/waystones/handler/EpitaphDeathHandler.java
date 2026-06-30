package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.LivingEntityCallback;
import net.blay09.mods.waystones.core.FleetingMemorialManager;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.message.ClientboundEpitaphActivationPacket;
import net.minecraft.server.level.ServerPlayer;

public class EpitaphDeathHandler {

    public static void register() {
        LivingEntityCallback.Death.Before.EVENT.register((entity, _) -> {
            if (entity instanceof ServerPlayer player) {
                trySpawnMemorial(player);
            }
            return true;
        });
    }

    private static void trySpawnMemorial(ServerPlayer player) {
        final var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            final var itemStack = inventory.getItem(i);
            if (itemStack.is(ModItems.epitaph.asItem()) && FleetingMemorialManager.spawnMemorial(player)) {
                itemStack.shrink(1);
                inventory.setChanged();
                Balm.networking().sendTo(player, ClientboundEpitaphActivationPacket.INSTANCE);
                return;
            }
        }
    }
}
