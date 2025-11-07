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

public record ClientboundWaystoneRemovedPacket(ResourceLocation waystoneType, UUID waystoneId, boolean wasDestroyed) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundWaystoneRemovedPacket> TYPE = new CustomPacketPayload.Type<>(id("waystone_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWaystoneRemovedPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ClientboundWaystoneRemovedPacket::waystoneType,
            UUIDUtil.STREAM_CODEC,
            ClientboundWaystoneRemovedPacket::waystoneId,
            ByteBufCodecs.BOOL,
            ClientboundWaystoneRemovedPacket::wasDestroyed,
            ClientboundWaystoneRemovedPacket::new
    );

    public static void handle(Player player, ClientboundWaystoneRemovedPacket message) {
        Balm.events().fireEvent(new WaystoneRemoveReceivedEvent(message.waystoneType, message.waystoneId, message.wasDestroyed));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
