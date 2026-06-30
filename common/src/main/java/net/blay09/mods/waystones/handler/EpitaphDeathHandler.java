package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.LivingDeathEvent;
import net.blay09.mods.waystones.core.FleetingMemorialManager;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.network.message.ClientboundEpitaphActivationPacket;
import net.minecraft.server.level.ServerPlayer;

public class EpitaphDeathHandler {

    public static void register() {
        Balm.getEvents().onEvent(LivingDeathEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                trySpawnMemorial(player);
            }
        });
    }

    private static void trySpawnMemorial(ServerPlayer player) {
        final var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            final var itemStack = inventory.getItem(i);
            if (itemStack.is(ModItems.epitaph) && FleetingMemorialManager.spawnMemorial(player)) {
                itemStack.shrink(1);
                inventory.setChanged();
                Balm.networking().sendTo(player, ClientboundEpitaphActivationPacket.INSTANCE);
                return;
            }
        }
    }
}
