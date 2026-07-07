package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.InventoryButtonMode;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionListBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public class ServerboundInventoryButtonPacket implements CustomPacketPayload {

    public static final ServerboundInventoryButtonPacket INSTANCE = new ServerboundInventoryButtonPacket();
    public static final CustomPacketPayload.Type<ServerboundInventoryButtonPacket> TYPE = new CustomPacketPayload.Type<>(id("inventory_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundInventoryButtonPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ServerboundInventoryButtonPacket() {
    }

    public static void handle(final ServerPlayer player, ServerboundInventoryButtonPacket message) {
        InventoryButtonMode inventoryButtonMode = WaystonesConfig.getActive().getInventoryButtonMode();
        if (!inventoryButtonMode.isEnabled()) {
            return;
        }

        final var waystone = PlayerWaystoneManager.getInventoryButtonTarget(player);
        if (waystone.isPresent()) {
            WaystonesAPI.createUncheckedDefaultTeleportContext(player, waystone.get(), it -> it.addFlag(TeleportFlags.INVENTORY_BUTTON))
                    .ifLeft(context -> WaystonesAPI.tryTeleportAsync(context)
                            .thenAccept(result -> result.ifRight(error -> player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED)))))
                    .ifRight(error -> player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED)));
        } else if (inventoryButtonMode.isReturnToAny()) {
            final var containerProvider = new WaystoneSelectionListBuilder(player)
                    .withInventoryButtonTargets()
                    .withFlags(Set.of(TeleportFlags.INVENTORY_BUTTON))
                    .buildMenuProvider(ModMenus.inventorySelection.value(), Component.translatable("container.waystones.waystone_selection"));
            Balm.networking().openMenu(player, containerProvider);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
