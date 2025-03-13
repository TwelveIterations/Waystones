package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRemoveWaystonePacket(UUID waystoneUid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundRemoveWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("remove_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRemoveWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundRemoveWaystonePacket::waystoneUid,
            ServerboundRemoveWaystonePacket::new
    );

    public static void handle(ServerPlayer player, ServerboundRemoveWaystonePacket message) {
        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        PlayerWaystoneManager.deactivateWaystone(player, waystone);


        if (player.getAbilities().instabuild) {
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                // If this is a sharestone and the player is in creative mode, remove the sharestone from the database
                SavedDataWaystonesStore.get(player.server).removeWaystone(waystone);
            } else if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
                // If the waystone is global and the player is in creative mode, remove the global-ness
                final var backingWaystone = waystone.getBackingWaystone();
                if (backingWaystone instanceof MutableWaystone mutableWaystone) {
                    mutableWaystone.setVisibility(WaystoneVisibility.ACTIVATION);

                    // Check if the waystone block still exists - if not, completely remove the waystone from existence to remove it from all players
                    // This way we can't have orphan global waystones left over. And just in case the waystone *was* just being silk-touch moved, it's easy to reactivate a global waystone for everyone (since it does that automatically).
                    ServerLevel targetWorld = Objects.requireNonNull(player.level().getServer()).getLevel(backingWaystone.getDimension());
                    BlockPos pos = backingWaystone.getPos();
                    BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
                    if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlock)) {
                        SavedDataWaystonesStore.get(player.server).removeWaystone(backingWaystone);
                        PlayerWaystoneManager.removeKnownWaystone(player.server, backingWaystone);
                        WaystoneSyncManager.sendWaystoneRemovalToAll(player.server, backingWaystone, true);
                    }
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
