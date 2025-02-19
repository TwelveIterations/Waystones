package net.blay09.mods.waystones.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.SyncConfigMessage;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.blay09.mods.waystones.network.message.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(id("inventory_button"), InventoryButtonMessage.class, InventoryButtonMessage::encode, InventoryButtonMessage::decode, InventoryButtonMessage::handle);
        networking.registerServerboundPacket(id("edit_waystone"), EditWaystoneMessage.class, EditWaystoneMessage::encode, EditWaystoneMessage::decode, EditWaystoneMessage::handle);
        networking.registerServerboundPacket(id("select_waystone"), SelectWaystoneMessage.class, SelectWaystoneMessage::encode, SelectWaystoneMessage::decode, SelectWaystoneMessage::handle);
        networking.registerServerboundPacket(id("sort_waystone"), SortWaystoneMessage.class, SortWaystoneMessage::encode, SortWaystoneMessage::decode, SortWaystoneMessage::handle);
        networking.registerServerboundPacket(id("remove_waystone"), RemoveWaystoneMessage.class, RemoveWaystoneMessage::encode, RemoveWaystoneMessage::decode, RemoveWaystoneMessage::handle);
        networking.registerServerboundPacket(id("request_edit_waystone"), RequestEditWaystoneMessage.class, RequestEditWaystoneMessage::encode, RequestEditWaystoneMessage::decode, RequestEditWaystoneMessage::handle);

        networking.registerClientboundPacket(id("waystone_update"), UpdateWaystoneMessage.class, UpdateWaystoneMessage::encode, UpdateWaystoneMessage::decode, UpdateWaystoneMessage::handle);
        networking.registerClientboundPacket(id("known_waystones"), KnownWaystonesMessage.class, KnownWaystonesMessage::encode, KnownWaystonesMessage::decode, KnownWaystonesMessage::handle);
        networking.registerClientboundPacket(id("teleport_effect"), TeleportEffectMessage.class, TeleportEffectMessage::encode, TeleportEffectMessage::decode, TeleportEffectMessage::handle);
        networking.registerClientboundPacket(id("waystone_cooldown"), PlayerWaystoneCooldownsMessage.class, PlayerWaystoneCooldownsMessage::encode, PlayerWaystoneCooldownsMessage::decode, PlayerWaystoneCooldownsMessage::handle);

        SyncConfigMessage.register(id("sync_config"), SyncWaystonesConfigMessage.class, SyncWaystonesConfigMessage::new, WaystonesConfigData.class, WaystonesConfigData::new);
    }

    @NotNull
    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

}
