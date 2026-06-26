package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundSortWaystoneGroupPacket(Identifier groupId, Identifier otherGroupId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSortWaystoneGroupPacket> TYPE = new CustomPacketPayload.Type<>(id("sort_waystone_group"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSortWaystoneGroupPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ServerboundSortWaystoneGroupPacket::groupId,
            Identifier.STREAM_CODEC,
            ServerboundSortWaystoneGroupPacket::otherGroupId,
            ServerboundSortWaystoneGroupPacket::new
    );

    public static final Identifier SORT_FIRST = id("sort_first");
    public static final Identifier SORT_LAST = id("sort_last");

    public static void handle(ServerPlayer player, ServerboundSortWaystoneGroupPacket message) {
        if (message.otherGroupId.equals(SORT_FIRST)) {
            PlayerWaystoneManager.sortWaystoneGroupAsFirst(player, message.groupId);
        } else if (message.otherGroupId.equals(SORT_LAST)) {
            PlayerWaystoneManager.sortWaystoneGroupAsLast(player, message.groupId);
        } else {
            PlayerWaystoneManager.sortWaystoneGroupSwap(player, message.groupId, message.otherGroupId);
        }
        WaystoneSyncManager.sendWaystoneGroups(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
