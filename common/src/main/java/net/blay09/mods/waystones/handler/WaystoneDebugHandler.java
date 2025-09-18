package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;

public class WaystoneDebugHandler {
    public static void onWaystoneUsed(UseBlockEvent event) {
        final var level = event.getLevel();
        final var player = event.getPlayer();
        final var heldItem = player.getItemInHand(event.getHand());
        if (player.getAbilities().instabuild) {
            final var blockEntity = level.getBlockEntity(event.getHitResult().getBlockPos());
            if (!(blockEntity instanceof WaystoneBlockEntityBase waystoneBase)) {
                return;
            }

            if (heldItem.getItem() == Items.BAMBOO) {
                if (!level.isClientSide()) {
                    waystoneBase.uninitializeWaystone();
                    player.displayClientMessage(Component.literal("Waystone was successfully reset - it will re-initialize once it is next loaded."), false);
                }
                event.setResult(InteractionResult.SUCCESS);
            } else if (heldItem.getItem() == Items.STICK) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.literal("Server UUID: " + waystoneBase.getWaystone().getWaystoneUid()), false);
                }
                if (level.isClientSide()) {
                    player.displayClientMessage(Component.literal("Client UUID: " + waystoneBase.getWaystone().getWaystoneUid()), false);
                }
                event.setResult(InteractionResult.SUCCESS);
            }
        }
    }
}
