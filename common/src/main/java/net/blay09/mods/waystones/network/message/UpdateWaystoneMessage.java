package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public record UpdateWaystoneMessage(Waystone waystone) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(id("update_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateWaystoneMessage> STREAM_CODEC = StreamCodec.composite(
            WaystoneImpl.STREAM_CODEC,
            UpdateWaystoneMessage::waystone,
            UpdateWaystoneMessage::new
    );

    public static void handle(Player player, UpdateWaystoneMessage message) {
        WaystoneManagerImpl.get(player.getServer()).updateWaystone(message.waystone);
        Balm.getEvents().fireEvent(new WaystoneUpdateReceivedEvent(message.waystone));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
