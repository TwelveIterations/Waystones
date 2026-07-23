package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundEditWaystoneGroupPacket(Identifier groupId, String name, Identifier icon, int color, boolean hidden) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundEditWaystoneGroupPacket> TYPE = new CustomPacketPayload.Type<>(id("edit_waystone_group"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEditWaystoneGroupPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ServerboundEditWaystoneGroupPacket::groupId,
            ByteBufCodecs.STRING_UTF8,
            ServerboundEditWaystoneGroupPacket::name,
            Identifier.STREAM_CODEC,
            ServerboundEditWaystoneGroupPacket::icon,
            ByteBufCodecs.INT,
            ServerboundEditWaystoneGroupPacket::color,
            ByteBufCodecs.BOOL,
            ServerboundEditWaystoneGroupPacket::hidden,
            ServerboundEditWaystoneGroupPacket::new
    );

    public static void handle(ServerPlayer player, ServerboundEditWaystoneGroupPacket message) {
        final var store = PlayerWaystoneManager.getPlayerWaystoneData(player.level());
        final var groups = new ArrayList<>(PlayerWaystoneManager.getWaystoneGroupRegistry(player));
        WaystoneGroup existingGroup = null;
        int existingGroupIndex = -1;
        for (int i = 0; i < groups.size(); i++) {
            final var group = groups.get(i);
            if (group.identifier().equals(message.groupId)) {
                existingGroup = group;
                existingGroupIndex = i;
                break;
            }
        }

        final var name = message.name.trim().isEmpty()
                ? Component.translatable("gui.waystones.manage_groups.unnamed_group")
                : Component.literal(message.name);
        final var hidden = existingGroup != null && existingGroup.inbuilt() && message.hidden;
        final var sortIndex = existingGroup != null ? existingGroup.sortIndex() : groups.size();
        final var updatedGroup = existingGroup != null
                ? new WaystoneGroupImpl(message.groupId, name, message.icon, message.color, existingGroup.inbuilt(), hidden, sortIndex)
                : new WaystoneGroupImpl(message.groupId, name, message.icon, message.color, false, false, sortIndex);
        if (existingGroupIndex != -1) {
            groups.set(existingGroupIndex, updatedGroup);
        } else {
            groups.add(updatedGroup);
        }
        store.setWaystoneGroupRegistry(player, groups);
        WaystoneSyncManager.sendWaystoneGroups(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
