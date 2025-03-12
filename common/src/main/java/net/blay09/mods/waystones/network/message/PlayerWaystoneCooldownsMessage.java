package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public record PlayerWaystoneCooldownsMessage(Map<ResourceLocation, Long> cooldowns) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerWaystoneCooldownsMessage> TYPE = new CustomPacketPayload.Type<>(id("player_waystone_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerWaystoneCooldownsMessage> STREAM_CODEC = StreamCodec.of(PlayerWaystoneCooldownsMessage::encode,
            PlayerWaystoneCooldownsMessage::decode);

    public static void encode(FriendlyByteBuf buf, PlayerWaystoneCooldownsMessage message) {
        buf.writeByte(message.cooldowns.size());
        for (Map.Entry<ResourceLocation, Long> entry : message.cooldowns.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeLong(entry.getValue());
        }
    }

    public static PlayerWaystoneCooldownsMessage decode(FriendlyByteBuf buf) {
        final var size = buf.readByte();
        final var cooldowns = new HashMap<ResourceLocation, Long>(size);
        for (var i = 0; i < size; i++) {
            cooldowns.put(buf.readResourceLocation(), buf.readLong());
        }
        return new PlayerWaystoneCooldownsMessage(cooldowns);
    }

    public static void handle(Player player, PlayerWaystoneCooldownsMessage message) {
        message.cooldowns.forEach((key, timestamp) -> PlayerWaystoneManager.setCooldownUntil(player, key, timestamp));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
