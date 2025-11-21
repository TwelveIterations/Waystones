package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.InteractionEventResult;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class WaystoneDebugHandler {
    public static void register() {
        BlockCallback.Use.EVENT.register((player, level, hand, hitResult) -> {
            final var heldItem = player.getItemInHand(hand);
            if (player.getAbilities().instabuild) {
                final var blockEntity = level.getBlockEntity(hitResult.getBlockPos());
                if (!(blockEntity instanceof WaystoneBlockEntityBase waystoneBase)) {
                    return InteractionEventResult.DEFAULT;
                }

                if (heldItem.getItem() == Items.BAMBOO) {
                    if (!level.isClientSide()) {
                        waystoneBase.uninitializeWaystone();
                        player.displayClientMessage(Component.literal("Waystone was successfully reset - it will re-initialize once it is next loaded."), false);
                    }
                    return InteractionEventResult.SUCCESS;
                } else if (heldItem.getItem() == Items.STICK) {
                    if (!level.isClientSide()) {
                        player.displayClientMessage(Component.literal("Server UUID: " + waystoneBase.getWaystone().getWaystoneUid()), false);
                    }
                    if (level.isClientSide()) {
                        player.displayClientMessage(Component.literal("Client UUID: " + waystoneBase.getWaystone().getWaystoneUid()), false);
                    }
                    return InteractionEventResult.SUCCESS;
                }
            }
            return InteractionEventResult.DEFAULT;
        });
    }
}
