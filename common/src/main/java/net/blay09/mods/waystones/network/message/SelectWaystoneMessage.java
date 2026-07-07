package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SelectWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "select_waystone"));

    private final UUID waystoneUid;

    public SelectWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(FriendlyByteBuf buf, SelectWaystoneMessage message) {
        buf.writeUUID(message.waystoneUid);
    }

    public static SelectWaystoneMessage decode(FriendlyByteBuf buf) {
        final var waystoneUid = buf.readUUID();
        return new SelectWaystoneMessage(waystoneUid);
    }

    public static void handle(final ServerPlayer player, SelectWaystoneMessage message) {
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
        final var waystone = selectedWaystone.get();
        if (selectionMenu.getType() == ModMenus.portalScrollSelection.get()) {
            WaystonesAPI.createUncheckedDefaultTeleportContext(player, waystone, it -> it.setWarpItem(selectionMenu.getWarpItem()))
                    .ifLeft(selectionMenu.getPostTeleportHandler())
                    .ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true));
            player.closeContainer();
            return;
        }

        final var warpHand = selectionMenu.getWarpHand();
        final var itemInHand = warpHand != null ? player.getItemInHand(warpHand) : ItemStack.EMPTY;
        if (warpHand != null && (itemInHand.isEmpty() || !ItemStack.isSameItemSameComponents(itemInHand, selectionMenu.getWarpItem()))) {
            final var error = new WaystoneTeleportError.SourceItemMissing();
            player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true);
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
                                .ifLeft(ignored -> selectionMenu.getPostTeleportHandler().accept(context))
                                .ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true))))
                .ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), true));
        player.closeContainer();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
