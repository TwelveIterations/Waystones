package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public record ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias, Set<Identifier> groupIds) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundPersonalWaystoneSettingsPacket> TYPE = new CustomPacketPayload.Type<>(id("personal_waystone_settings"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPersonalWaystoneSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ServerboundPersonalWaystoneSettingsPacket::waystoneUid,
            ComponentSerialization.OPTIONAL_STREAM_CODEC,
            ServerboundPersonalWaystoneSettingsPacket::alias,
            ByteBufCodecs.collection(HashSet::new, Identifier.STREAM_CODEC),
            ServerboundPersonalWaystoneSettingsPacket::groupIds,
            ServerboundPersonalWaystoneSettingsPacket::new
    );

    public static void handle(ServerPlayer player, ServerboundPersonalWaystoneSettingsPacket message) {
        final var server = player.level().getServer();
        final var waystone = new WaystoneProxy(server, message.waystoneUid);
        if (!waystone.isValid()) {
            Waystones.logger.warn("{} tried to decorate an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        PlayerWaystoneManager.setWaystoneAlias(player, waystone.getWaystoneUid(), message.alias.orElse(null));
        PlayerWaystoneManager.setConfiguredWaystoneGroups(player, waystone.getWaystoneUid(), message.groupIds);
        WaystoneSyncManager.sendActivatedWaystones(player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
