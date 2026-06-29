package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.PersonalWaystoneSettingsMenu;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundRequestPersonalWaystoneSettingsPacket(UUID waystoneUid) implements CustomPacketPayload {

    public static final Type<ServerboundRequestPersonalWaystoneSettingsPacket> TYPE = new Type<>(id("request_personal_waystone_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestPersonalWaystoneSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundRequestPersonalWaystoneSettingsPacket::waystoneUid,
            ServerboundRequestPersonalWaystoneSettingsPacket::new
    );

    public static void handle(ServerPlayer player, ServerboundRequestPersonalWaystoneSettingsPacket message) {
        PlayerWaystoneManager.findWaystone(player, message.waystoneUid)
                .ifPresent(waystone -> Balm.networking().openMenu(player, PersonalWaystoneSettingsMenu.getProvider(player, waystone)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
