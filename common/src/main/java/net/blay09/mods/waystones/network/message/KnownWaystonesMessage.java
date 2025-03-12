package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public record KnownWaystonesMessage(ResourceLocation waystoneType, List<Waystone> waystones) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KnownWaystonesMessage> TYPE = new CustomPacketPayload.Type<>(id("known_waystones"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KnownWaystonesMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            KnownWaystonesMessage::waystoneType,
            WaystoneImpl.LIST_STREAM_CODEC,
            KnownWaystonesMessage::waystones,
            KnownWaystonesMessage::new
    );

    public static void handle(Player player, KnownWaystonesMessage message) {
        final var waystones = message.waystones.stream().toList(); // backwards compat for event expecting a List
        if (message.waystoneType.equals(WaystoneTypes.WAYSTONE)) {
            InMemoryPlayerWaystoneData playerWaystoneData = (InMemoryPlayerWaystoneData) PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT);
            playerWaystoneData.setWaystones(message.waystones);
        }

        Balm.getEvents().fireEvent(new WaystonesListReceivedEvent(message.waystoneType, waystones));

        for (Waystone waystone : message.waystones) {
            WaystoneManagerImpl.get(player.getServer()).updateWaystone(waystone);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
