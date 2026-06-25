package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public class ClientboundWaystoneGroupsPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundWaystoneGroupsPacket> TYPE = new CustomPacketPayload.Type<>(id("waystone_groups"));
    private final List<WaystoneGroup> groups;

    public ClientboundWaystoneGroupsPacket(List<WaystoneGroup> groups) {
        this.groups = groups;
    }

    public static void encode(RegistryFriendlyByteBuf buf, ClientboundWaystoneGroupsPacket message) {
        buf.writeShort(message.groups.size());
        for (final var group : message.groups) {
            WaystoneGroupImpl.STREAM_CODEC.encode(buf, group);
        }
    }

    public static ClientboundWaystoneGroupsPacket decode(RegistryFriendlyByteBuf buf) {
        final var groupCount = buf.readShort();
        final var groups = new ArrayList<WaystoneGroup>();
        for (int i = 0; i < groupCount; i++) {
            groups.add(WaystoneGroupImpl.STREAM_CODEC.decode(buf));
        }
        return new ClientboundWaystoneGroupsPacket(groups);
    }

    public static void handle(Player player, ClientboundWaystoneGroupsPacket message) {
        PlayerWaystoneManager.getPlayerWaystoneData(BalmEnvironment.CLIENT).setWaystoneGroupRegistry(player, message.groups);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
