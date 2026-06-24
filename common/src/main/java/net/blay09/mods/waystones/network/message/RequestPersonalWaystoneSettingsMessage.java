package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RequestPersonalWaystoneSettingsMessage implements CustomPacketPayload {

    public static final Type<RequestPersonalWaystoneSettingsMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID,
            "request_personal_waystone_settings"));

    private final BlockPos pos;

    public RequestPersonalWaystoneSettingsMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(FriendlyByteBuf buf, RequestPersonalWaystoneSettingsMessage message) {
        buf.writeBlockPos(message.pos);
    }

    public static RequestPersonalWaystoneSettingsMessage decode(FriendlyByteBuf buf) {
        final var pos = buf.readBlockPos();
        return new RequestPersonalWaystoneSettingsMessage(pos);
    }

    public static void handle(ServerPlayer player, RequestPersonalWaystoneSettingsMessage message) {
        final var level = player.level();
        final var blockEntity = level.isLoaded(message.pos) ? level.getBlockEntity(message.pos) : null;
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getPersonalSettingsMenuProvider(player)
                    .ifPresent(menuProvider -> Balm.getNetworking().openGui(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
