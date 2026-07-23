package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.store.SavedDataWaystonesStore;
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

public record ServerboundEditWaystonePacket(UUID waystoneUid, String name, WaystoneVisibility visibility) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundEditWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("edit_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEditWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundEditWaystonePacket::waystoneUid,
            ByteBufCodecs.STRING_UTF8,
            ServerboundEditWaystonePacket::name,
            ByteBufCodecs.idMapper(it -> WaystoneVisibility.values()[it], WaystoneVisibility::ordinal),
            ServerboundEditWaystonePacket::visibility,
            ServerboundEditWaystonePacket::new
    );

    public static void handle(ServerPlayer player, ServerboundEditWaystonePacket message) {
        final var server = player.level().getServer();
        final var waystone = new WaystoneProxy(server, message.waystoneUid);
        if (!waystone.isValid()) {
            Waystones.logger.warn("{} tried to edit an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        final var pos = waystone.getPos();
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var error = WaystonePermissionManager.mayEditWaystone(player, waystone);
        if (error.isPresent()) {
            return;
        }

        var visibility = message.visibility;
        final var visibilityOptions = WaystoneVisibilities.getVisibilityOptions(player, waystone);
        if (!visibilityOptions.contains(message.visibility)) {
            Waystones.logger.warn("{} tried to edit a waystone with an invalid visibility {}", player.getName().getString(), message.visibility);
            visibility = visibilityOptions.getFirst();
        }

        final var backingWaystone = (WaystoneImpl) waystone.getBackingWaystone();
        final var legalName = makeNameLegal(server, message.name);
        backingWaystone.setName(legalName);

        final var previousVisibility = backingWaystone.getVisibility();
        backingWaystone.setVisibility(visibility);

        WaystoneIndexManager.visibilityChanged(server, backingWaystone, previousVisibility);
        SavedDataWaystonesStore.get(server).setDirty();
        WaystoneSyncManager.sendWaystoneUpdateToAll(server, backingWaystone);
    }

    private static Component makeNameLegal(MinecraftServer server, String input) {
        if (input.trim().isEmpty()) {
            return Component.translatable("waystones.untitled_waystone");
        }
        final var inventoryButtonMode = WaystonesConfig.getActive().inventoryButton.inventoryButton;
        if (inventoryButtonMode.equals(input) && SavedDataWaystonesStore.get(server).findWaystoneByName(input).isPresent()) {
            return Component.literal(input + "*");
        }

        return Component.literal(input);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
