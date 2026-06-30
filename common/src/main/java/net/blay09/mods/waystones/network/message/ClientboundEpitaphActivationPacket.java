package net.blay09.mods.waystones.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.blay09.mods.waystones.client.ClientEpitaphActivationHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public class ClientboundEpitaphActivationPacket implements CustomPacketPayload {

    public static final ClientboundEpitaphActivationPacket INSTANCE = new ClientboundEpitaphActivationPacket();
    public static final Type<ClientboundEpitaphActivationPacket> TYPE = new Type<>(id("epitaph_activation"));

    private ClientboundEpitaphActivationPacket() {
    }

    public static void encode(FriendlyByteBuf buf, ClientboundEpitaphActivationPacket message) {
    }

    public static ClientboundEpitaphActivationPacket decode(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    public static void handle(Player player, ClientboundEpitaphActivationPacket message) {
        ClientEpitaphActivationHandler.playEffects();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
