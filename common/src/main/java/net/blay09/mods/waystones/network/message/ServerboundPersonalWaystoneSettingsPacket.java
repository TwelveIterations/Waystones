package net.blay09.mods.waystones.network.message;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.PersonalizedWaystoneImpl;
import net.blay09.mods.waystones.core.WaystoneSyncManager;
import net.minecraft.network.FriendlyByteBuf;
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
    private final boolean hidden;

    public ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias) {
        this(waystoneUid, alias, Set.of(), false);
    }

    public ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias, Set<ResourceLocation> groupIds) {
        this(waystoneUid, alias, groupIds, false);
    }

    public ServerboundPersonalWaystoneSettingsPacket(UUID waystoneUid, Optional<Component> alias, Set<ResourceLocation> groupIds, boolean hidden) {
        this.waystoneUid = waystoneUid;
        this.alias = alias;
        this.groupIds = groupIds;
        this.hidden = hidden;
    }

    public static void encode(RegistryFriendlyByteBuf buf, ServerboundPersonalWaystoneSettingsPacket message) {
        buf.writeUUID(message.waystoneUid);
        ComponentSerialization.OPTIONAL_STREAM_CODEC.encode(buf, message.alias);
        buf.writeCollection(message.groupIds, FriendlyByteBuf::writeResourceLocation);
        buf.writeBoolean(message.hidden);
    }

    public static ServerboundPersonalWaystoneSettingsPacket decode(RegistryFriendlyByteBuf buf) {
        return new ServerboundPersonalWaystoneSettingsPacket(buf.readUUID(),
                ComponentSerialization.OPTIONAL_STREAM_CODEC.decode(buf),
                buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation),
                buf.readBoolean());
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
        PlayerWaystoneManager.setWaystoneHidden(player, resolvedWaystone.getWaystoneUid(), message.hidden);
        WaystoneSyncManager.sendActivatedWaystones(player);
        if (resolvedWaystone.isTransient()) {
            Balm.networking().sendTo(player, new UpdateWaystoneMessage(new PersonalizedWaystoneImpl(resolvedWaystone, message.alias.orElse(null), message.groupIds, message.hidden)));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
