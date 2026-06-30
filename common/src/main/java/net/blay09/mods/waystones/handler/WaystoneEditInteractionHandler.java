package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.InteractionEventResult;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;

public class WaystoneEditInteractionHandler {
    public static void register() {
        BlockCallback.Use.EVENT.register((player, level, hand, hitResult) -> {
            if (!player.isShiftKeyDown()) {
                return InteractionEventResult.DEFAULT;
            }

            final var itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof BlockItem) {
                return InteractionEventResult.DEFAULT;
            }

            final var pos = hitResult.getBlockPos();
            final var blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity)) {
                return InteractionEventResult.DEFAULT;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                waystoneBlockEntity.getSettingsMenuProvider(serverPlayer)
                        .ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
            }

            return InteractionEventResult.SUCCESS;
        });
    }

}
