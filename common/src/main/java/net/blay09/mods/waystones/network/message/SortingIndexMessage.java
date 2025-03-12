package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.core.InMemoryPlayerWaystoneData;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record SortingIndexMessage(List<UUID> sortingIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SortingIndexMessage> TYPE = new CustomPacketPayload.Type<>(id("sorting_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SortingIndexMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, UUIDUtil.STREAM_CODEC),
            SortingIndexMessage::sortingIndex,
            SortingIndexMessage::new
    );

    public static void handle(Player player, SortingIndexMessage message) {
        final var playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
        playerWaystoneData.setSortingIndex(player, message.sortingIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
