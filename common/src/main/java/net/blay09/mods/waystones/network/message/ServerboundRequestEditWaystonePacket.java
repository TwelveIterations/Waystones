package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRequestEditWaystonePacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundRequestEditWaystonePacket> TYPE = new CustomPacketPayload.Type<>(id("request_edit_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestEditWaystonePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ServerboundRequestEditWaystonePacket::pos,
            ServerboundRequestEditWaystonePacket::new
    );

    public static void handle(ServerPlayer player, ServerboundRequestEditWaystonePacket message) {
        final var pos = message.pos;
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var blockEntity = player.level().getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getSettingsMenuProvider().ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

