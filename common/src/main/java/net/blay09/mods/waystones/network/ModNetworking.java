package net.blay09.mods.waystones.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.network.message.*;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(InventoryButtonMessage.TYPE, InventoryButtonMessage.class, InventoryButtonMessage.STREAM_CODEC, InventoryButtonMessage::handle);
        networking.registerServerboundPacket(EditWaystoneMessage.TYPE, EditWaystoneMessage.class, EditWaystoneMessage.STREAM_CODEC, EditWaystoneMessage::handle);
        networking.registerServerboundPacket(SelectWaystoneMessage.TYPE, SelectWaystoneMessage.class, SelectWaystoneMessage.STREAM_CODEC, SelectWaystoneMessage::handle);
        networking.registerServerboundPacket(SortWaystoneMessage.TYPE, SortWaystoneMessage.class, SortWaystoneMessage.STREAM_CODEC, SortWaystoneMessage::handle);
        networking.registerServerboundPacket(RemoveWaystoneMessage.TYPE, RemoveWaystoneMessage.class, RemoveWaystoneMessage.STREAM_CODEC, RemoveWaystoneMessage::handle);
        networking.registerServerboundPacket(RequestEditWaystoneMessage.TYPE, RequestEditWaystoneMessage.class, RequestEditWaystoneMessage.STREAM_CODEC, RequestEditWaystoneMessage::handle);
        networking.registerServerboundPacket(RequestManageWaystoneModifiersMessage.TYPE, RequestManageWaystoneModifiersMessage.class, RequestManageWaystoneModifiersMessage.STREAM_CODEC, RequestManageWaystoneModifiersMessage::handle);

        networking.registerClientboundPacket(UpdateWaystoneMessage.TYPE, UpdateWaystoneMessage.class, UpdateWaystoneMessage.STREAM_CODEC, UpdateWaystoneMessage::handle);
        networking.registerClientboundPacket(WaystoneRemovedMessage.TYPE, WaystoneRemovedMessage.class, WaystoneRemovedMessage.STREAM_CODEC, WaystoneRemovedMessage::handle);
        networking.registerClientboundPacket(KnownWaystonesMessage.TYPE, KnownWaystonesMessage.class, KnownWaystonesMessage.STREAM_CODEC, KnownWaystonesMessage::handle);
        networking.registerClientboundPacket(SortingIndexMessage.TYPE, SortingIndexMessage.class, SortingIndexMessage.STREAM_CODEC, SortingIndexMessage::handle);
        networking.registerClientboundPacket(TeleportEffectMessage.TYPE, TeleportEffectMessage.class, TeleportEffectMessage.STREAM_CODEC, TeleportEffectMessage::handle);
        networking.registerClientboundPacket(PlayerWaystoneCooldownsMessage.TYPE, PlayerWaystoneCooldownsMessage.class, PlayerWaystoneCooldownsMessage.STREAM_CODEC, PlayerWaystoneCooldownsMessage::handle);
        networking.registerClientboundPacket(WarpPlateEjectEffectMessage.TYPE, WarpPlateEjectEffectMessage.class, WarpPlateEjectEffectMessage.STREAM_CODEC, WarpPlateEjectEffectMessage::handle);

        SyncConfigMessage.register(SyncWaystonesConfigMessage.TYPE, SyncWaystonesConfigMessage.class, SyncWaystonesConfigMessage::new, WaystonesConfigData.class, WaystonesConfigData::new);
    }

}
