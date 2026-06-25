package net.blay09.mods.waystones.network;

import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.waystones.network.message.*;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(ServerboundInventoryButtonPacket.TYPE, ServerboundInventoryButtonPacket.class, ServerboundInventoryButtonPacket.STREAM_CODEC, ServerboundInventoryButtonPacket::handle);
        networking.registerServerboundPacket(ServerboundEditWaystonePacket.TYPE, ServerboundEditWaystonePacket.class, ServerboundEditWaystonePacket.STREAM_CODEC, ServerboundEditWaystonePacket::handle);
        networking.registerServerboundPacket(ServerboundPersonalWaystoneSettingsPacket.TYPE, ServerboundPersonalWaystoneSettingsPacket.class, ServerboundPersonalWaystoneSettingsPacket.STREAM_CODEC, ServerboundPersonalWaystoneSettingsPacket::handle);
        networking.registerServerboundPacket(ServerboundSelectWaystonePacket.TYPE, ServerboundSelectWaystonePacket.class, ServerboundSelectWaystonePacket.STREAM_CODEC, ServerboundSelectWaystonePacket::handle);
        networking.registerServerboundPacket(ServerboundSortWaystonePacket.TYPE, ServerboundSortWaystonePacket.class, ServerboundSortWaystonePacket.STREAM_CODEC, ServerboundSortWaystonePacket::handle);
        networking.registerServerboundPacket(ServerboundRemoveWaystonePacket.TYPE, ServerboundRemoveWaystonePacket.class, ServerboundRemoveWaystonePacket.STREAM_CODEC, ServerboundRemoveWaystonePacket::handle);
        networking.registerServerboundPacket(ServerboundRequestEditWaystonePacket.TYPE, ServerboundRequestEditWaystonePacket.class, ServerboundRequestEditWaystonePacket.STREAM_CODEC, ServerboundRequestEditWaystonePacket::handle);
        networking.registerServerboundPacket(ServerboundRequestInventoryButtonPacket.TYPE, ServerboundRequestInventoryButtonPacket.class, ServerboundRequestInventoryButtonPacket.STREAM_CODEC, ServerboundRequestInventoryButtonPacket::handle);
        networking.registerServerboundPacket(ServerboundRequestManageWaystoneModifiersPacket.TYPE, ServerboundRequestManageWaystoneModifiersPacket.class, ServerboundRequestManageWaystoneModifiersPacket.STREAM_CODEC, ServerboundRequestManageWaystoneModifiersPacket::handle);
        networking.registerServerboundPacket(ServerboundRequestPersonalWaystoneSettingsPacket.TYPE, ServerboundRequestPersonalWaystoneSettingsPacket.class, ServerboundRequestPersonalWaystoneSettingsPacket.STREAM_CODEC, ServerboundRequestPersonalWaystoneSettingsPacket::handle);

        networking.registerClientboundPacket(ClientboundUpdateWaystonePacket.TYPE, ClientboundUpdateWaystonePacket.class, ClientboundUpdateWaystonePacket.STREAM_CODEC, ClientboundUpdateWaystonePacket::handle);
        networking.registerClientboundPacket(ClientboundWaystoneRemovedPacket.TYPE, ClientboundWaystoneRemovedPacket.class, ClientboundWaystoneRemovedPacket.STREAM_CODEC, ClientboundWaystoneRemovedPacket::handle);
        networking.registerClientboundPacket(ClientboundWaystoneGroupsPacket.TYPE, ClientboundWaystoneGroupsPacket.class, ClientboundWaystoneGroupsPacket.STREAM_CODEC, ClientboundWaystoneGroupsPacket::handle);
        networking.registerClientboundPacket(ClientboundKnownWaystonesPacket.TYPE, ClientboundKnownWaystonesPacket.class, ClientboundKnownWaystonesPacket.STREAM_CODEC, ClientboundKnownWaystonesPacket::handle);
        networking.registerClientboundPacket(ClientboundSortingIndexPacket.TYPE, ClientboundSortingIndexPacket.class, ClientboundSortingIndexPacket.STREAM_CODEC, ClientboundSortingIndexPacket::handle);
        networking.registerClientboundPacket(ClientboundTeleportEffectPacket.TYPE, ClientboundTeleportEffectPacket.class, ClientboundTeleportEffectPacket.STREAM_CODEC, ClientboundTeleportEffectPacket::handle);
    }

}
