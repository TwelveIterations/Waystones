package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundSortWaystoneGroupPacket(ResourceLocation groupId, ResourceLocation otherGroupId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSortWaystoneGroupPacket> TYPE = new CustomPacketPayload.Type<>(id("sort_waystone_group"));

    public static final ResourceLocation SORT_FIRST = id("sort_first");
    public static final ResourceLocation SORT_LAST = id("sort_last");

    public static void encode(FriendlyByteBuf buf, ServerboundSortWaystoneGroupPacket message) {
        buf.writeResourceLocation(message.groupId);
        buf.writeResourceLocation(message.otherGroupId);
    }

    public static ServerboundSortWaystoneGroupPacket decode(FriendlyByteBuf buf) {
        final var groupId = buf.readResourceLocation();
        final var otherGroupId = buf.readResourceLocation();
        return new ServerboundSortWaystoneGroupPacket(groupId, otherGroupId);
    }

    public static void handle(ServerPlayer player, ServerboundSortWaystoneGroupPacket message) {
        if (player == null) {
            return;
        }

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
