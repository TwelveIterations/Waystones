package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRequestEditWaystonePacket(UUID waystoneUid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundRequestEditWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("request_edit_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestEditWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundRequestEditWaystonePacket::waystoneUid,
            ServerboundRequestEditWaystonePacket::new
    );

    public static void handle(ServerPlayer player, ServerboundRequestEditWaystonePacket message) {
        final var server = player.level().getServer();
        final var waystone = new WaystoneProxy(server, message.waystoneUid);
        if (!waystone.isValid()) {
            Waystones.logger.warn("{} tried to request editing an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        final var level = server.getLevel(waystone.getDimension());
        final var pos = waystone.getPos();
        if (level == null || level != player.level()) {
            return;
        }

        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var blockEntity = level.isLoaded(pos) ? level.getBlockEntity(pos) : null;
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getSettingsMenuProvider(player).ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
