package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
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
            final var waystones = PlayerWaystoneManager.getPlayerDecoratedWaystones(player, PlayerWaystoneManager.getTargetsForInventoryButton(player));
            PlayerWaystoneManager.ensureSortingIndex(player, waystones);
            final var containerProvider = new BalmMenuProvider<List<UserDecoratedWaystone>>() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.waystones.waystone_selection");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                    return new WaystoneSelectionMenu(ModMenus.inventorySelection.get(), null, windowId, waystones, Set.of(TeleportFlags.INVENTORY_BUTTON));
                }

                @Override
                public List<UserDecoratedWaystone> getScreenOpeningData(ServerPlayer serverPlayer) {
                    return waystones;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, List<UserDecoratedWaystone>> getScreenStreamCodec() {
                    return UserDecoratedWaystone.LIST_STREAM_CODEC;
                }
            };
            Balm.getNetworking().openGui(player, containerProvider);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
