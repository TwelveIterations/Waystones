package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundPlayerWaystoneCooldownsPacket(Map<Identifier, Long> cooldowns) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundPlayerWaystoneCooldownsPacket> TYPE = new CustomPacketPayload.Type<>(id("player_waystone_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerWaystoneCooldownsPacket> STREAM_CODEC = StreamCodec.of(
            ClientboundPlayerWaystoneCooldownsPacket::encode,
            ClientboundPlayerWaystoneCooldownsPacket::decode);

    public static void encode(FriendlyByteBuf buf, ClientboundPlayerWaystoneCooldownsPacket message) {
        buf.writeByte(message.cooldowns.size());
        for (Map.Entry<Identifier, Long> entry : message.cooldowns.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeLong(entry.getValue());
        }
    }

    public static ClientboundPlayerWaystoneCooldownsPacket decode(FriendlyByteBuf buf) {
        final var size = buf.readByte();
        final var cooldowns = new HashMap<Identifier, Long>(size);
        for (var i = 0; i < size; i++) {
            cooldowns.put(buf.readIdentifier(), buf.readLong());
        }
        return new ClientboundPlayerWaystoneCooldownsPacket(cooldowns);
    }

    public static void handle(Player player, ClientboundPlayerWaystoneCooldownsPacket message) {
        PlayerWaystoneManager.resetCooldowns(player);
        message.cooldowns.forEach((key, timestamp) -> PlayerWaystoneManager.setCooldownUntil(player, key, timestamp));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
