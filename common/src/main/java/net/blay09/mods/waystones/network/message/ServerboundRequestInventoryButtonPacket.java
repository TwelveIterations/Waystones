package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;

import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.rules.WaystonesEffectExecutors;
import net.blay09.mods.waystones.core.WaystoneTeleportContextImpl;
import net.blay09.mods.waystones.core.InvalidWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.waystones.Waystones.id;

public class ServerboundRequestInventoryButtonPacket implements CustomPacketPayload {

    public static final ServerboundRequestInventoryButtonPacket INSTANCE = new ServerboundRequestInventoryButtonPacket();
    public static final Type<ServerboundRequestInventoryButtonPacket> TYPE = new Type<>(id("request_inventory_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestInventoryButtonPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ServerboundRequestInventoryButtonPacket() {
    }

    public static void handle(ServerPlayer player, ServerboundRequestInventoryButtonPacket message) {
        if (!WaystonesConfig.getActive().getInventoryButtonMode().isEnabled()) {
            return;
        }

        final var waystone = PlayerWaystoneManager.getInventoryButtonTarget(player).orElse(InvalidWaystone.INSTANCE);
        final var context = WaystonesAPI.createUnboundTeleportContext(player, waystone).addFlag(TeleportFlags.INVENTORY_BUTTON);
        if (context instanceof WaystoneTeleportContextImpl impl) {
            impl.setExecutor(WaystonesEffectExecutors.simulated());
        }
        Balm.networking().sendTo(player, new ClientboundInventoryButtonPacket(context.getRequirements()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
