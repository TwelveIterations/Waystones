package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.platform.BalmEnvironment;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.store.InMemoryWaystonesPlayerStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundKnownWaystonesPacket(Identifier waystoneType, List<Waystone> waystones) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundKnownWaystonesPacket> TYPE = new CustomPacketPayload.Type<>(id("known_waystones"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundKnownWaystonesPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ClientboundKnownWaystonesPacket::waystoneType,
            WaystoneImpl.LIST_STREAM_CODEC,
            ClientboundKnownWaystonesPacket::waystones,
            ClientboundKnownWaystonesPacket::new
    );

    public static void handle(Player player, ClientboundKnownWaystonesPacket message) {
        if (message.waystoneType.equals(WaystoneKinds.WAYSTONE)) {
            InMemoryWaystonesPlayerStore playerWaystoneData = (InMemoryWaystonesPlayerStore) PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
            playerWaystoneData.setWaystones(message.waystones);
        }

        WaystonesListReceivedEvent.EVENT.invoker().accept(new WaystonesListReceivedEvent(message.waystoneType, message.waystones));

        for (Waystone waystone : message.waystones) {
            WaystonesClient.getWaystonesStore().updateWaystone(waystone);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
