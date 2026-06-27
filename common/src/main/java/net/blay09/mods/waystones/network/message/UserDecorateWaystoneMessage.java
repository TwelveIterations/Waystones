package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneProxy;
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

public class UserDecorateWaystoneMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UserDecorateWaystoneMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "user_decorate_waystone"));

    private final UUID waystoneUid;
    private final Optional<Component> alias;
    private final Set<ResourceLocation> groupIds;

    public UserDecorateWaystoneMessage(UUID waystoneUid, Optional<Component> alias) {
        this(waystoneUid, alias, Set.of());
    }

    public UserDecorateWaystoneMessage(UUID waystoneUid, Optional<Component> alias, Set<ResourceLocation> groupIds) {
        this.waystoneUid = waystoneUid;
        this.alias = alias;
        this.groupIds = groupIds;
    }

    public static void encode(RegistryFriendlyByteBuf buf, UserDecorateWaystoneMessage message) {
        buf.writeUUID(message.waystoneUid);
        ComponentSerialization.OPTIONAL_STREAM_CODEC.encode(buf, message.alias);
        buf.writeCollection(message.groupIds, (innerBuf, groupId) -> innerBuf.writeResourceLocation(groupId));
    }

    public static UserDecorateWaystoneMessage decode(RegistryFriendlyByteBuf buf) {
        return new UserDecorateWaystoneMessage(buf.readUUID(),
                ComponentSerialization.OPTIONAL_STREAM_CODEC.decode(buf),
                buf.readCollection(HashSet::new, innerBuf -> innerBuf.readResourceLocation()));
    }

    public static void handle(ServerPlayer player, UserDecorateWaystoneMessage message) {
        final var waystone = new WaystoneProxy(player.server, message.waystoneUid);
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
