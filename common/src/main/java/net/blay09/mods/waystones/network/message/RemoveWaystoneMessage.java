package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.Waystone;
import net.blay09.mods.waystones.core.WaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;

public class RemoveWaystoneMessage {

    private final UUID waystoneUid;

    public RemoveWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(RemoveWaystoneMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.waystoneUid);
    }

    public static RemoveWaystoneMessage decode(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        return new RemoveWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, RemoveWaystoneMessage message) {
        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        PlayerWaystoneManager.deactivateWaystone(player, waystone);

        // If the waystone is global and the player is in creative mode, remove the global-ness
        if (waystone.isGlobal() && player.getAbilities().instabuild) {
            IWaystone backingWaystone = waystone.getBackingWaystone();
            if (backingWaystone instanceof Waystone) {
                ((Waystone) backingWaystone).setGlobal(false);

                // Check if the waystone block still exists - if not, completely remove the waystone from existence to remove it from all players
                // This way we can't have orphan global waystones left over. And just in case the waystone *was* just being silk-touch moved, it's easy to reactivate a global waystone for everyone (since it does that automatically).
                ServerLevel targetWorld = Objects.requireNonNull(player.level().getServer()).getLevel(backingWaystone.getDimension());
                BlockPos pos = backingWaystone.getPos();
                BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
                if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlock)) {
                    WaystoneManager.get(player.server).removeWaystone(backingWaystone);
                    PlayerWaystoneManager.removeKnownWaystone(player.server, backingWaystone);
                }
            }
        }
    }

}
