package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;

public class WaystoneEditInteractionHandler {
    public static void register() {
        BlockCallback.Use.EVENT.register((player, level, hand, hitResult) -> {
            if (!player.isShiftKeyDown()) {
                return InteractionResult.PASS;
            }

            final var itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof BlockItem) {
                return InteractionResult.PASS;
            }

            final var pos = hitResult.getBlockPos();
            final var blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                waystoneBlockEntity.getSettingsMenuProvider()
                        .ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
            }

            return InteractionResult.SUCCESS;
        });
    }

}
