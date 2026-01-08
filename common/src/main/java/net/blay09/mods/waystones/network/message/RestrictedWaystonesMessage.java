package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.core.WaystoneManagerImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class RestrictedWaystonesMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RestrictedWaystonesMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID, "restricted_waystones"));

    private final ResourceLocation type;
    private final List<SharestoneSyncData> waystones;

    public RestrictedWaystonesMessage(ResourceLocation type, List<SharestoneSyncData> waystones) {
        this.type = type;
        this.waystones = waystones;
    }

    public static void encode(RegistryFriendlyByteBuf buf, RestrictedWaystonesMessage message) {
        buf.writeResourceLocation(message.type);
        buf.writeShort(message.waystones.size());
        for (SharestoneSyncData waystone : message.waystones) {
            SharestoneSyncData.encode(buf, waystone);
        }
    }

    public static RestrictedWaystonesMessage decode(RegistryFriendlyByteBuf buf) {
        final var type = buf.readResourceLocation();
        final var waystoneCount = buf.readShort();
        final List<SharestoneSyncData> waystones = new ArrayList<>();
        for (int i = 0; i < waystoneCount; i++) {
            waystones.add(SharestoneSyncData.decode(buf));
        }
        return new RestrictedWaystonesMessage(type, waystones);
    }

    public static void handle(Player player, RestrictedWaystonesMessage message) {
        final List<Waystone> restrictedWaystones = new ArrayList<>();
        for (SharestoneSyncData data : message.waystones) {
            final var restrictedWaystone = data.toRestrictedWaystone();
            restrictedWaystones.add(restrictedWaystone);
            WaystoneManagerImpl.get(player.getServer()).updateWaystone(restrictedWaystone);
        }
        Balm.getEvents().fireEvent(new WaystonesListReceivedEvent(message.type, restrictedWaystones));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
