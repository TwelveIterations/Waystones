package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;

public class RemoveWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RemoveWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "remove_waystone"));

    private final UUID waystoneUid;

    public RemoveWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(FriendlyByteBuf buf, RemoveWaystoneMessage message) {
        buf.writeUUID(message.waystoneUid);
    }

    public static RemoveWaystoneMessage decode(FriendlyByteBuf buf) {
        UUID waystoneUid = buf.readUUID();
        return new RemoveWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, RemoveWaystoneMessage message) {
        WaystoneProxy waystone = new WaystoneProxy(player.server, message.waystoneUid);
        PlayerWaystoneManager.deactivateWaystone(player, waystone);


        if (player.getAbilities().instabuild) {
            if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
                // If this is a sharestone and the player is in creative mode, remove the sharestone from the database
                WaystoneManagerImpl.get(player.server).removeWaystone(waystone);
            } else if (waystone.getVisibility() == WaystoneVisibility.GLOBAL) {
                // If the waystone is global and the player is in creative mode, remove the global-ness
                final var backingWaystone = waystone.getBackingWaystone();
                if (backingWaystone instanceof MutableWaystone mutableWaystone) {
                    final var previousVisibility = backingWaystone.getVisibility();
                    mutableWaystone.setVisibility(WaystoneVisibility.ACTIVATION);
                    TeamWaystoneManager.visibilityChanged(player.server, backingWaystone, previousVisibility);

                    // Check if the waystone block still exists - if not, completely remove the waystone from existence to remove it from all players
                    // This way we can't have orphan global waystones left over. And just in case the waystone *was* just being silk-touch moved, it's easy to reactivate a global waystone for everyone (since it does that automatically).
                    ServerLevel targetWorld = Objects.requireNonNull(player.level().getServer()).getLevel(backingWaystone.getDimension());
                    BlockPos pos = backingWaystone.getPos();
                    BlockState state = targetWorld != null ? targetWorld.getBlockState(pos) : null;
                    if (targetWorld == null || !(state.getBlock() instanceof WaystoneBlock)) {
                        WaystoneManagerImpl.get(player.server).removeWaystone(backingWaystone);
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
