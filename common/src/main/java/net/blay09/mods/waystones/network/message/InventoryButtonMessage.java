package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class InventoryButtonMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<InventoryButtonMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "inventory_button"));

    public static void encode(FriendlyByteBuf buf, InventoryButtonMessage message) {
    }

    public static InventoryButtonMessage decode(FriendlyByteBuf buf) {
        return new InventoryButtonMessage();
    }

    public static void handle(final ServerPlayer player, InventoryButtonMessage message) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        if (player == null) {
            return;
        }

        // Reset cooldown if player is in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.resetCooldowns(player);
        }

        final var waystone = PlayerWaystoneManager.getInventoryButtonTarget(player);
        if (waystone.isPresent()) {
            WaystonesAPI.createUncheckedDefaultTeleportContext(player, waystone.get(), it -> it.addFlag(TeleportFlags.INVENTORY_BUTTON))
                    .ifLeft(context -> WaystonesAPI.tryTeleportAsync(context)
                            .thenAccept(result -> result.ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true))))
                    .ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true));
        } else if (inventoryButtonMode.isReturnToAny()) {
            final var menuProvider = new WaystoneSelectionListBuilder(player)
                    .withInventoryButtonTargets()
                    .withFlags(Set.of(TeleportFlags.INVENTORY_BUTTON))
                    .buildMenuProvider(ModMenus.inventorySelection.get(), Component.translatable("container.waystones.waystone_selection"));
            Balm.networking().openMenu(player, menuProvider);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
