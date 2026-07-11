package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RequestManageWaystoneModifiersMessage implements CustomPacketPayload {

    public static final Type<RequestManageWaystoneModifiersMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID,
            "request_manage_waystone_modifiers"));

    private final BlockPos pos;

    public RequestManageWaystoneModifiersMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(FriendlyByteBuf buf, RequestManageWaystoneModifiersMessage message) {
        buf.writeBlockPos(message.pos);
    }

    public static RequestManageWaystoneModifiersMessage decode(FriendlyByteBuf buf) {
        final var pos = buf.readBlockPos();
        return new RequestManageWaystoneModifiersMessage(pos);
    }

    public static void handle(ServerPlayer player, RequestManageWaystoneModifiersMessage message) {
        if (!WaystonesConfig.getActive().teleports.enableModifiers) {
            return;
        }
        final var level = player.level();
        final var blockEntity = level.isLoaded(message.pos) ? level.getBlockEntity(message.pos) : null;
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            if (player.distanceToSqr(waystoneBlockEntity.getWaystone().getPos().getCenter()) > 64) {
                return;
            }
            waystoneBlockEntity.getModifierMenuProvider(player)
                    .ifPresent(menuProvider -> Balm.getNetworking().openGui(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
