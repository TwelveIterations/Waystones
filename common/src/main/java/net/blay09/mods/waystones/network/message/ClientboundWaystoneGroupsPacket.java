package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.platform.BalmEnvironment;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundWaystoneGroupsPacket(List<WaystoneGroup> groups) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundWaystoneGroupsPacket> TYPE = new CustomPacketPayload.Type<>(id("waystone_groups"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWaystoneGroupsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, WaystoneGroupImpl.STREAM_CODEC),
            ClientboundWaystoneGroupsPacket::groups,
            ClientboundWaystoneGroupsPacket::new
    );

    public static void handle(Player player, ClientboundWaystoneGroupsPacket message) {
        PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT).setWaystoneGroupRegistry(player, message.groups);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
