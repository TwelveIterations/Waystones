package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record EditWaystoneMessage(UUID waystoneUid, String name, WaystoneVisibility visibility) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EditWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(id("edit_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EditWaystoneMessage> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            EditWaystoneMessage::waystoneUid,
            ByteBufCodecs.STRING_UTF8,
            EditWaystoneMessage::name,
            ByteBufCodecs.idMapper(it -> WaystoneVisibility.values()[it], WaystoneVisibility::ordinal),
            EditWaystoneMessage::visibility,
            EditWaystoneMessage::new
    );

    public static void handle(ServerPlayer player, EditWaystoneMessage message) {
        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
        if (!waystone.isValid()) {
            Waystones.logger.warn("{} tried to edit an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), waystone);
        if (error.isPresent()) {
            return;
        }

        var visibility = message.visibility;
        final var visibilityOptions = WaystoneVisibilities.getVisibilityOptions(player, waystone);
        if (!visibilityOptions.contains(message.visibility)) {
            Waystones.logger.warn("{} tried to edit a waystone with an invalid visibility {}", player.getName().getString(), message.visibility);
            visibility = visibilityOptions.getFirst();
        }

        if (!WaystonePermissionManager.isAllowedVisibility(visibility) && !WaystonePermissionManager.skipsPermissions(player)) {
            Waystones.logger.warn("{} tried to edit a restricted waystone without permission", player.getName().getString());
            return;
        }

        final var pos = waystone.getPos();
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var backingWaystone = (WaystoneImpl) waystone.getBackingWaystone();
        final var legalName = makeNameLegal(player.server, message.name);
        backingWaystone.setName(legalName);

        if (visibility == WaystoneVisibility.GLOBAL && (WaystonePermissionManager.isAllowedVisibility(visibility) || WaystonePermissionManager.skipsPermissions(
                player))) {
            if (backingWaystone.getVisibility() != WaystoneVisibility.GLOBAL) {
                PlayerWaystoneManager.activeWaystoneForEveryone(player.server, backingWaystone);
            }
        }
        backingWaystone.setVisibility(visibility);

        WaystoneManagerImpl.get(player.server).setDirty();
        WaystoneSyncManager.sendWaystoneUpdateToAll(player.server, backingWaystone);

        player.closeContainer();
    }

    private static Component makeNameLegal(MinecraftServer server, String input) {
        if (input.trim().isEmpty()) {
            return Component.translatable("waystones.untitled_waystone");
        }
        final var inventoryButtonMode = WaystonesConfig.getActive().inventoryButton.inventoryButton;
        if (inventoryButtonMode.equals(input) && WaystoneManagerImpl.get(server).findWaystoneByName(input).isPresent()) {
            return Component.literal(input + "*");
        }

        return Component.literal(input);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
