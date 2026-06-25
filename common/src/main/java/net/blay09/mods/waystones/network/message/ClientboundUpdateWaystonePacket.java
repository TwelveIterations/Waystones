package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundUpdateWaystonePacket(UserDecoratedWaystone waystone) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundUpdateWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("update_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            UserDecoratedWaystone.STREAM_CODEC,
            ClientboundUpdateWaystonePacket::waystone,
            ClientboundUpdateWaystonePacket::new
    );

    public static void handle(Player player, ClientboundUpdateWaystonePacket message) {
        message.waystone.getAlias().ifPresent(alias -> PlayerWaystoneManager.setWaystoneAlias(player, message.waystone.getWaystoneUid(), alias));
        PlayerWaystoneManager.setConfiguredWaystoneGroups(player, message.waystone.getWaystoneUid(), message.waystone.getConfiguredGroups());
        WaystonesClient.getWaystonesStore().updateWaystone(message.waystone);
        WaystoneUpdateReceivedEvent.EVENT.invoker().accept(new WaystoneUpdateReceivedEvent(message.waystone()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
