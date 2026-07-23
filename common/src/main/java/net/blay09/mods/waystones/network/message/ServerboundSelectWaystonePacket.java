package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundSelectWaystonePacket(UUID waystoneUid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSelectWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("select_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSelectWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundSelectWaystonePacket::waystoneUid,
            ServerboundSelectWaystonePacket::new
    );

    public static void handle(final ServerPlayer player, ServerboundSelectWaystonePacket message) {
        if (!(player.containerMenu instanceof WaystoneSelectionMenu selectionMenu)) {
            return;
        }

        final var selectedWaystone = selectionMenu.getWaystones().stream()
                .filter(it -> it.getWaystoneUid().equals(message.waystoneUid))
                .findFirst();
        if (selectedWaystone.isEmpty()) {
            Waystones.logger.warn("{} tried to teleport to waystone {} that they don't have access to.",
                    player.getName().getString(),
                    message.waystoneUid);
            return;
        }
        final var waystone = PlayerWaystoneManager.findWaystone(player, message.waystoneUid).orElse(null);
        if (waystone == null) {
            Waystones.logger.warn("{} tried to teleport to transient waystone {} that is no longer available.",
                    player.getName().getString(),
                    message.waystoneUid);
            final var error = new WaystoneTeleportError.TeleportNoLongerValid();
            player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED));
            player.closeContainer();
            return;
        }

        if (selectionMenu.getType() == ModMenus.portalScrollSelection.value()) {
            WaystonesAPI.createUncheckedDefaultTeleportContext(player, waystone, it -> {
                        it.setWarpItem(selectionMenu.getWarpItem());
                        it.setWarpHand(selectionMenu.getWarpHand());
                    })
                    .ifLeft(selectionMenu.getPostTeleportHandler())
                    .ifRight(error -> player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED)));
            player.closeContainer();
            return;
        }

        final var warpHand = selectionMenu.getWarpHand();
        final var itemInHand = warpHand != null ? player.getItemInHand(warpHand) : ItemStack.EMPTY;
        if (warpHand != null && (itemInHand.isEmpty() || !ItemStack.isSameItemSameComponents(itemInHand, selectionMenu.getWarpItem()))) {
            final var error = new WaystoneTeleportError.SourceItemMissing();
            player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED));
            player.closeContainer();
            return;
        }

        WaystonesAPI.createUncheckedDefaultTeleportContext(player, waystone, it -> {
                    it.setFromWaystone(selectionMenu.getWaystoneFrom());
                    it.setWarpItem(selectionMenu.getWarpItem());
                    if (warpHand != null) {
                        it.setWarpHand(warpHand);
                    }
                    it.addFlags(selectionMenu.getFlags());
                })
                .ifLeft(context -> WaystonesAPI.tryTeleportAsync(context)
                        .thenAccept(result -> result
                                .ifLeft(_ -> selectionMenu.getPostTeleportHandler().accept(context))
                                .ifRight(error -> player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED)))))
                .ifRight(error -> player.sendOverlayMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED)));
        player.closeContainer();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
