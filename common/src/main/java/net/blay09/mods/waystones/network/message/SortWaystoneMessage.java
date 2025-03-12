package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record SortWaystoneMessage(UUID waystoneUid, UUID otherWaystoneUid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SortWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(id("sort_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SortWaystoneMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            SortWaystoneMessage::waystoneUid,
            UUIDUtil.STREAM_CODEC,
            SortWaystoneMessage::otherWaystoneUid,
            SortWaystoneMessage::new
    );

    public static final UUID SORT_FIRST = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final UUID SORT_LAST = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    public static void handle(ServerPlayer player, SortWaystoneMessage message) {
        if (player == null) {
            return;
        }

        if (message.otherWaystoneUid.equals(SORT_FIRST)) {
            PlayerWaystoneManager.sortWaystoneAsFirst(player, message.waystoneUid);
        } else if (message.otherWaystoneUid.equals(SORT_LAST)) {
            PlayerWaystoneManager.sortWaystoneAsLast(player, message.waystoneUid);
        } else {
            PlayerWaystoneManager.sortWaystoneSwap(player, message.waystoneUid, message.otherWaystoneUid);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
