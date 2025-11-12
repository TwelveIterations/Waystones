package net.blay09.mods.waystones.handler;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;

public class WaystoneEditInteractionHandler {
    public static void onUseBlock(UseBlockEvent event) {
        final var level = event.getLevel();
        final var player = event.getPlayer();
        if (player == null) {
            return;
        }

        if (!player.isShiftKeyDown()) {
            return;
        }

        final var itemStack = player.getItemInHand(event.getHand());
        if (itemStack.getItem() instanceof BlockItem) {
            return;
        }

        final var pos = event.getHitResult().getBlockPos();
        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity)) {
            return;
        }

        if (!level.isClientSide()) {
            waystoneBlockEntity.getSettingsMenuProvider()
                    .ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
        }

        event.setResult(InteractionResult.SUCCESS);
    }
}
