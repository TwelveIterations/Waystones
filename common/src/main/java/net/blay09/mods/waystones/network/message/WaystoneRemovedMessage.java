package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.event.WaystoneRemoveReceivedEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record WaystoneRemovedMessage(ResourceLocation waystoneType, UUID waystoneId, boolean wasDestroyed) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WaystoneRemovedMessage> TYPE = new CustomPacketPayload.Type<>(id("waystone_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneRemovedMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            WaystoneRemovedMessage::waystoneType,
            UUIDUtil.STREAM_CODEC,
            WaystoneRemovedMessage::waystoneId,
            ByteBufCodecs.BOOL,
            WaystoneRemovedMessage::wasDestroyed,
            WaystoneRemovedMessage::new
    );

    public static void handle(Player player, WaystoneRemovedMessage message) {
        Balm.getEvents().fireEvent(new WaystoneRemoveReceivedEvent(message.waystoneType, message.waystoneId, message.wasDestroyed));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
