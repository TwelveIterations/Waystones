package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.core.TwinboundFeatherTargets;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
        final var waystone = selectedWaystone.get().isTransient()
                ? TwinboundFeatherTargets.findTarget(player, message.waystoneUid).orElse(null)
                : new WaystoneProxy(player.level().getServer(), message.waystoneUid);
        if (waystone == null) {
            Waystones.logger.warn("{} tried to teleport to transient waystone {} that is no longer available.",
                    player.getName().getString(),
                    message.waystoneUid);
            return;
        }

        if (selectionMenu.getType() == ModMenus.portalScrollSelection.get()) {
            WaystonesAPI.createDefaultTeleportContext(player, waystone, it -> {
                        it.setWarpItem(selectionMenu.getWarpItem());
                    })
                    .ifLeft(selectionMenu.getPostTeleportHandler());
            player.closeContainer();
            return;
        }

        WaystonesAPI.createDefaultTeleportContext(player, waystone, it -> {
                    it.setFromWaystone(selectionMenu.getWaystoneFrom());
                    it.setWarpItem(selectionMenu.getWarpItem());
                    it.addFlags(selectionMenu.getFlags());
                })
                .ifLeft(WaystonesAPI::tryTeleport)
                .ifLeft(selectionMenu.getPostTeleportHandler())
                .ifRight(error -> player.displayClientMessage(error.getComponent().copy().withStyle(ChatFormatting.DARK_RED), false));
        player.closeContainer();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
