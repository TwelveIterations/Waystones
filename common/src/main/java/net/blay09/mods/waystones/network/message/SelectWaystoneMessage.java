package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record SelectWaystoneMessage(UUID waystoneUid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(id("select_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectWaystoneMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SelectWaystoneMessage::waystoneUid,
            SelectWaystoneMessage::new
    );

    public static void handle(final ServerPlayer player, SelectWaystoneMessage message) {
        if (!(player.containerMenu instanceof WaystoneSelectionMenu selectionMenu)) {
            return;
        }

        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
        if (selectionMenu.getWaystones().stream().noneMatch(it -> it.getWaystoneUid().equals(waystone.getWaystoneUid()))) {
            Waystones.logger.warn("{} tried to teleport to waystone {} that they don't have access to.",
                    player.getName().getString(),
                    waystone.getWaystoneUid());
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
