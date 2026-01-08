package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.core.RestrictedWaystone;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record SharestoneSyncData(UUID waystoneId, ResourceLocation waystoneType, Component displayName, WaystoneVisibility visibility, WaystoneOrigin origin,
                                 ResourceKey<Level> dimension, int distance) {

    public static SharestoneSyncData fromWaystone(ServerPlayer player, Waystone waystone) {
        int distance = -1;
        if (player.level().dimension().equals(waystone.getDimension())) {
            distance = (int) player.position().distanceTo(waystone.getPos().getCenter());
        }
        return new SharestoneSyncData(waystone.getWaystoneUid(), waystone.getWaystoneType(), waystone.getName(), waystone.getVisibility(),
                waystone.getOrigin(), waystone.getDimension(), distance);
    }

    public static SharestoneSyncData decode(RegistryFriendlyByteBuf buf) {
        final var waystoneId = buf.readUUID();
        final var waystoneType = buf.readResourceLocation();
        final var displayName = ComponentSerialization.STREAM_CODEC.decode(buf);
        final var visibility = buf.readEnum(WaystoneVisibility.class);
        final var origin = buf.readEnum(WaystoneOrigin.class);
        final var dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        final var distance = buf.readInt();
        return new SharestoneSyncData(waystoneId, waystoneType, displayName, visibility, origin, dimension, distance);
    }

    public static void encode(RegistryFriendlyByteBuf buf, SharestoneSyncData data) {
        buf.writeUUID(data.waystoneId());
        buf.writeResourceLocation(data.waystoneType());
        ComponentSerialization.STREAM_CODEC.encode(buf, data.displayName());
        buf.writeEnum(data.visibility());
        buf.writeEnum(data.origin());
        buf.writeResourceLocation(data.dimension().location());
        buf.writeInt(data.distance());
    }

    public RestrictedWaystone toRestrictedWaystone() {
        return new RestrictedWaystone(waystoneType, waystoneId, origin, dimension, displayName, visibility, distance);
    }
}
