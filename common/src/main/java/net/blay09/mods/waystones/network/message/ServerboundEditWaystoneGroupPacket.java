package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneGroupImpl;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundEditWaystoneGroupPacket(ResourceLocation groupId, String name, ResourceLocation icon, int color, boolean hidden) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundEditWaystoneGroupPacket> TYPE = new CustomPacketPayload.Type<>(id("edit_waystone_group"));

    public static void encode(FriendlyByteBuf buf, ServerboundEditWaystoneGroupPacket message) {
        buf.writeResourceLocation(message.groupId);
        buf.writeUtf(message.name);
        buf.writeResourceLocation(message.icon);
        buf.writeInt(message.color);
        buf.writeBoolean(message.hidden);
    }

    public static ServerboundEditWaystoneGroupPacket decode(FriendlyByteBuf buf) {
        final var groupId = buf.readResourceLocation();
        final var name = buf.readUtf(128);
        final var icon = buf.readResourceLocation();
        final var color = buf.readInt();
        final var hidden = buf.readBoolean();
        return new ServerboundEditWaystoneGroupPacket(groupId, name, icon, color, hidden);
    }

    public static void handle(ServerPlayer player, ServerboundEditWaystoneGroupPacket message) {
        final var store = PlayerWaystoneManager.getPlayerWaystoneData(player.level());
        final var groups = new ArrayList<WaystoneGroup>();
        WaystoneGroup existingGroup = null;
        for (final var group : store.getWaystoneGroupRegistry(player)) {
            if (group.identifier().equals(message.groupId)) {
                existingGroup = group;
                continue;
            }

            groups.add(group);
        }

        final var name = message.name.trim().isEmpty()
                ? Component.translatable("gui.waystones.manage_groups.unnamed_group")
                : Component.literal(message.name);
        final var hidden = existingGroup != null && existingGroup.inbuilt() && message.hidden;
        final var sortIndex = existingGroup != null ? existingGroup.sortIndex() : groups.size();
        groups.add(existingGroup != null
                ? new WaystoneGroupImpl(message.groupId, name, message.icon, message.color, existingGroup.inbuilt(), hidden, sortIndex)
                : new WaystoneGroupImpl(message.groupId, name, message.icon, message.color, false, false, sortIndex));
        store.setWaystoneGroupRegistry(player, groups);
        WaystoneSyncManager.sendWaystoneGroups(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
