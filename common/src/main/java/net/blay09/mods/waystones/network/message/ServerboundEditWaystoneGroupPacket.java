package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.GroupValidation;
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
        buf.writeUtf(message.groupId.toString(), GroupValidation.MAX_IDENTIFIER_LENGTH);
        buf.writeUtf(message.name, GroupValidation.MAX_NAME_LENGTH);
        buf.writeUtf(message.icon.toString(), GroupValidation.MAX_IDENTIFIER_LENGTH);
        buf.writeInt(message.color);
        buf.writeBoolean(message.hidden);
    }

    public static ServerboundEditWaystoneGroupPacket decode(FriendlyByteBuf buf) {
        final var groupId = ResourceLocation.parse(buf.readUtf(GroupValidation.MAX_IDENTIFIER_LENGTH));
        final var name = buf.readUtf(GroupValidation.MAX_NAME_LENGTH);
        final var icon = ResourceLocation.parse(buf.readUtf(GroupValidation.MAX_IDENTIFIER_LENGTH));
        final var color = buf.readInt();
        final var hidden = buf.readBoolean();
        return new ServerboundEditWaystoneGroupPacket(groupId, name, icon, color, hidden);
    }


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

        if (existingGroup == null && GroupValidation.getCustomGroupCount(groups) >= GroupValidation.MAX_CUSTOM_GROUPS) {
            return;
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
