package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.client.ClientEpitaphActivationHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public class ClientboundEpitaphActivationPacket implements CustomPacketPayload {

    public static final ClientboundEpitaphActivationPacket INSTANCE = new ClientboundEpitaphActivationPacket();
    public static final Type<ClientboundEpitaphActivationPacket> TYPE = new Type<>(id("epitaph_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEpitaphActivationPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientboundEpitaphActivationPacket() {
    }

    public static void handle(Player player, ClientboundEpitaphActivationPacket message) {
        ClientEpitaphActivationHandler.playEffects();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
