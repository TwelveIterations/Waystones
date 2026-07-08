package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.WaystoneSortMode;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundSetPreferencesPacket(WaystoneSortMode sortMode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSetPreferencesPacket> TYPE = new CustomPacketPayload.Type<>(id("serverbound_set_preferences"));

    public static void encode(FriendlyByteBuf buf, ServerboundSetPreferencesPacket message) {
        buf.writeEnum(message.sortMode);
    }

    public static ServerboundSetPreferencesPacket decode(FriendlyByteBuf buf) {
        return new ServerboundSetPreferencesPacket(buf.readEnum(WaystoneSortMode.class));
    }

    public static void handle(ServerPlayer player, ServerboundSetPreferencesPacket message) {
        PlayerWaystoneManager.setWaystoneSortMode(player, message.sortMode);
        Balm.networking().sendTo(player, new ClientboundSetPreferencesPacket(message.sortMode));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
