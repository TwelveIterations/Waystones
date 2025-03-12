package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record RequestEditWaystoneMessage(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestEditWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(id("request_edit_waystone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestEditWaystoneMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RequestEditWaystoneMessage::pos,
            RequestEditWaystoneMessage::new
    );

    public static void handle(ServerPlayer player, RequestEditWaystoneMessage message) {
        final var pos = message.pos;
        if (player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) > 64) {
            return;
        }

        final var blockEntity = player.level().getBlockEntity(pos);
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getSettingsMenuProvider().ifPresent(menuProvider -> Balm.getNetworking().openMenu(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

