package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.menu.PersonalWaystoneSettingsMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class RequestPersonalWaystoneSettingsMessage implements CustomPacketPayload {

    public static final Type<RequestPersonalWaystoneSettingsMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            Waystones.MOD_ID,
            "request_personal_waystone_settings"));

    private final UUID waystoneUid;

    public RequestPersonalWaystoneSettingsMessage(UUID waystoneUid) {
        this.waystoneUid = waystoneUid;
    }

    public static void encode(FriendlyByteBuf buf, RequestPersonalWaystoneSettingsMessage message) {
        buf.writeUUID(message.waystoneUid);
    }

    public static RequestPersonalWaystoneSettingsMessage decode(FriendlyByteBuf buf) {
        final var waystoneUid = buf.readUUID();
        return new RequestPersonalWaystoneSettingsMessage(waystoneUid);
    }

    public static void handle(ServerPlayer player, RequestPersonalWaystoneSettingsMessage message) {
        final var waystone = new WaystoneProxy(player.level().getServer(), message.waystoneUid);
        if (waystone.isValid()) {
            Balm.getNetworking().openGui(player, PersonalWaystoneSettingsMenu.getProvider(player, waystone));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
