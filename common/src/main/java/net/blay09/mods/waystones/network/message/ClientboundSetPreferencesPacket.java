package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.WaystoneSortMode;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundSetPreferencesPacket(WaystoneSortMode sortMode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSetPreferencesPacket> TYPE = new CustomPacketPayload.Type<>(id("clientbound_set_preferences"));

    public static void encode(FriendlyByteBuf buf, ClientboundSetPreferencesPacket message) {
        buf.writeEnum(message.sortMode);
    }

    public static ClientboundSetPreferencesPacket decode(FriendlyByteBuf buf) {
        return new ClientboundSetPreferencesPacket(buf.readEnum(WaystoneSortMode.class));
    }

    public static void handle(Player player, ClientboundSetPreferencesPacket message) {
        PlayerWaystoneManager.setWaystoneSortMode(player, message.sortMode);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
