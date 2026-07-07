package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class RequestEditWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestEditWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID,
            "request_edit_waystone"));

    private final UUID waystoneUid;

    public RequestEditWaystoneMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(FriendlyByteBuf buf, RequestEditWaystoneMessage message) {
        buf.writeUUID(message.waystoneUid);
    }

    public static RequestEditWaystoneMessage decode(FriendlyByteBuf buf) {
        final var waystoneUid = buf.readUUID();
        return new RequestEditWaystoneMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, RequestEditWaystoneMessage message) {
        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
        if (!waystone.isValid()) {
            Waystones.logger.warn("{} tried to request editing an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        final var level = player.server.getLevel(waystone.getDimension());
        if (level == null || level != player.level() || player.distanceToSqr(waystone.getPos().getCenter()) > 64) {
            return;
        }

        final var blockEntity = level.isLoaded(waystone.getPos()) ? level.getBlockEntity(waystone.getPos()) : null;
        if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntity) {
            waystoneBlockEntity.getSettingsMenuProvider(player)
                    .ifPresent(menuProvider -> Balm.getNetworking().openGui(player, menuProvider));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
