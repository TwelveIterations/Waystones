package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerWaystoneCooldownsMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerWaystoneCooldownsMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "player_waystone_cooldowns"));

    private final Map<ResourceLocation, Long> cooldowns;

    public PlayerWaystoneCooldownsMessage(Map<ResourceLocation, Long> cooldowns) {
        this.cooldowns = cooldowns;
    }

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
        PlayerWaystoneManager.resetCooldowns(player);
        message.cooldowns.forEach((key, timestamp) -> PlayerWaystoneManager.setCooldownUntil(player, key, timestamp));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
