package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRequestPersonalWaystoneSettingsPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ServerboundRequestPersonalWaystoneSettingsPacket> TYPE = new Type<>(id("request_personal_waystone_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestPersonalWaystoneSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ServerboundRequestPersonalWaystoneSettingsPacket::pos,
            ServerboundRequestPersonalWaystoneSettingsPacket::new
    );

    public static void handle(ServerPlayer player, ServerboundRequestPersonalWaystoneSettingsPacket message) {
        final var blockEntity = player.level().getBlockEntity(message.pos);
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getPersonalSettingsMenuProvider(player).ifPresent(menuProvider -> Balm.networking().openMenu(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
