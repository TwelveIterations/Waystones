package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.UserDecoratedWaystone;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.blay09.mods.waystones.Waystones.id;

public class ServerboundPersonalWaystoneSettingsPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundPersonalWaystoneSettingsPacket> TYPE = new CustomPacketPayload.Type<>(id("personal_waystone_settings"));

    private final UUID waystoneUid;
    private final Optional<Component> alias;
    private final Set<ResourceLocation> groupIds;

    public ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias) {
        this(waystoneUid, alias, Set.of());
    }

    public ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias, Set<ResourceLocation> groupIds) {
        this.waystoneUid = waystoneUid;
        this.alias = alias;
        this.groupIds = groupIds;
    }

    public static void encode(RegistryFriendlyByteBuf buf, ServerboundPersonalWaystoneSettingsPacket message) {
        buf.writeUUID(message.waystoneUid);
        ComponentSerialization.OPTIONAL_STREAM_CODEC.encode(buf, message.alias);
        buf.writeCollection(message.groupIds, (innerBuf, groupId) -> innerBuf.writeResourceLocation(groupId));
    }

    public static ServerboundPersonalWaystoneSettingsPacket decode(RegistryFriendlyByteBuf buf) {
        return new ServerboundPersonalWaystoneSettingsPacket(buf.readUUID(),
                ComponentSerialization.OPTIONAL_STREAM_CODEC.decode(buf),
                buf.readCollection(HashSet::new, innerBuf -> innerBuf.readResourceLocation()));
    }

    public static void handle(ServerPlayer player, ServerboundPersonalWaystoneSettingsPacket message) {
        final var waystone = PlayerWaystoneManager.findWaystone(player, message.waystoneUid);
        if (waystone.isEmpty()) {
            Waystones.logger.warn("{} tried to decorate an invalid waystone with id {}", player.getName().getString(), message.waystoneUid);
            return;
        }

        final var resolvedWaystone = waystone.get();
        PlayerWaystoneManager.setWaystoneAlias(player, resolvedWaystone.getWaystoneUid(), message.alias.orElse(null));
        PlayerWaystoneManager.setConfiguredWaystoneGroups(player, resolvedWaystone.getWaystoneUid(), message.groupIds);
        WaystoneSyncManager.sendActivatedWaystones(player);
        if (resolvedWaystone.isTransient()) {
            Balm.getNetworking().sendTo(player, new UpdateWaystoneMessage(new UserDecoratedWaystone(resolvedWaystone, message.alias.orElse(null), message.groupIds)));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
