package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class InventoryButtonMessage {

    public static void encode(InventoryButtonMessage message, FriendlyByteBuf buf) {
    }

    public static InventoryButtonMessage decode(FriendlyByteBuf buf) {
        return new InventoryButtonMessage();
    }

    public static void handle(ServerPlayer player, InventoryButtonMessage message) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        if (player == null) {
            return;
        }

        // Reset cooldown if player is in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, 0);
        }

        if (!PlayerWaystoneManager.canUseInventoryButton(player)) {
            return;
        }

        IWaystone waystone = PlayerWaystoneManager.getInventoryButtonWaystone(player);
        if (waystone != null) {
            PlayerWaystoneManager.tryTeleportToWaystone(player, waystone, WarpMode.INVENTORY_BUTTON, null);
        } else if (inventoryButtonMode.isReturnToAny()) {
            final BalmMenuProvider containerProvider = new BalmMenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                    return WaystoneSelectionMenu.createWaystoneSelection(i, playerEntity, WarpMode.INVENTORY_BUTTON, null);
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    buf.writeByte(WarpMode.INVENTORY_BUTTON.ordinal());
                }
            };
            Balm.getNetworking().openGui(player, containerProvider);
        }
    }

}
