package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class PlayerWaystoneCooldownsMessage {

    private final long inventoryButtonCooldownUntil;
    private final long warpStoneCooldownUntil;

    public PlayerWaystoneCooldownsMessage(long inventoryButtonCooldownUntil, long warpStoneCooldownUntil) {
        this.inventoryButtonCooldownUntil = inventoryButtonCooldownUntil;
        this.warpStoneCooldownUntil = warpStoneCooldownUntil;
    }

    public static void encode(PlayerWaystoneCooldownsMessage message, FriendlyByteBuf buf) {
        buf.writeLong(message.inventoryButtonCooldownUntil);
        buf.writeLong(message.warpStoneCooldownUntil);
    }

    public static PlayerWaystoneCooldownsMessage decode(FriendlyByteBuf buf) {
        long inventoryButtonCooldownUntil = buf.readLong();
        long warpStoneCooldownUntil = buf.readLong();
        return new PlayerWaystoneCooldownsMessage(inventoryButtonCooldownUntil, warpStoneCooldownUntil);
    }

    public static void handle(Player player, PlayerWaystoneCooldownsMessage message) {
        PlayerWaystoneManager.setInventoryButtonCooldownUntil(player, message.inventoryButtonCooldownUntil);
        PlayerWaystoneManager.setWarpStoneCooldownUntil(player, message.warpStoneCooldownUntil);
    }
}
