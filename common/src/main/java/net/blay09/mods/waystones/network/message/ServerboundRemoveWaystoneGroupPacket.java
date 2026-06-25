package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashSet;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRemoveWaystoneGroupPacket(ResourceLocation groupId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundRemoveWaystoneGroupPacket> TYPE = new CustomPacketPayload.Type<>(id("remove_waystone_group"));

    public static void encode(FriendlyByteBuf buf, ServerboundRemoveWaystoneGroupPacket message) {
        buf.writeResourceLocation(message.groupId);
    }

    public static ServerboundRemoveWaystoneGroupPacket decode(FriendlyByteBuf buf) {
        return new ServerboundRemoveWaystoneGroupPacket(buf.readResourceLocation());
    }

    public static void handle(ServerPlayer player, ServerboundRemoveWaystoneGroupPacket message) {
        final var store = PlayerWaystoneManager.getPlayerWaystoneData(player.level());
        final var groups = new ArrayList<WaystoneGroup>();
        var removed = false;
        for (final var group : store.getWaystoneGroupRegistry(player)) {
            if (group.identifier().equals(message.groupId)) {
                if (group.inbuilt()) {
                    return;
                }
                removed = true;
                continue;
            }

            groups.add(group);
        }

        if (!removed) {
            return;
        }

        store.setWaystoneGroupRegistry(player, groups);
        for (final var waystone : store.getWaystones(player)) {
            final var configuredGroups = new HashSet<>(store.getConfiguredWaystoneGroups(player, waystone.getWaystoneUid()));
            if (configuredGroups.remove(message.groupId)) {
                store.setConfiguredWaystoneGroups(player, waystone.getWaystoneUid(), configuredGroups);
            }
        }
        WaystoneSyncManager.sendWaystoneGroups(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
