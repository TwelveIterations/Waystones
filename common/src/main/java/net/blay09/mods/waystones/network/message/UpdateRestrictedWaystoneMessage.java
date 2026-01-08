package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.core.WaystoneManagerImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UpdateRestrictedWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateRestrictedWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID, "update_restricted_waystone"));

    private final SharestoneSyncData data;

    public UpdateRestrictedWaystoneMessage(SharestoneSyncData data) {
        this.data = data;
    }

    public static void encode(RegistryFriendlyByteBuf buf, UpdateRestrictedWaystoneMessage message) {
        SharestoneSyncData.encode(buf, message.data);
    }

    public static UpdateRestrictedWaystoneMessage decode(RegistryFriendlyByteBuf buf) {
        return new UpdateRestrictedWaystoneMessage(SharestoneSyncData.decode(buf));
    }

    public static void handle(Player player, UpdateRestrictedWaystoneMessage message) {
        final var restrictedWaystone = message.data.toRestrictedWaystone();
        WaystoneManagerImpl.get(player.getServer()).updateWaystone(restrictedWaystone);
        Balm.getEvents().fireEvent(new WaystoneUpdateReceivedEvent(restrictedWaystone));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
