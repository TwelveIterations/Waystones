package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SharestoneSelectionEntry(UUID id, Component name, int distanceMeters, ResourceLocation waystoneType,
                                       WaystoneVisibility visibility, WaystoneOrigin origin, ResourceKey<Level> dimension) {

    public static final StreamCodec<RegistryFriendlyByteBuf, SharestoneSelectionEntry> STREAM_CODEC = StreamCodec.of(
            SharestoneSelectionEntry::write,
            SharestoneSelectionEntry::read);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<SharestoneSelectionEntry>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
            ByteBufCodecs.collection(ArrayList::new));

    public static SharestoneSelectionEntry fromWaystone(ServerPlayer player, Waystone waystone) {
        int distance = -1;
        if (player.level().dimension().equals(waystone.getDimension())) {
            distance = (int) player.position().distanceTo(waystone.getPos().getCenter());
        }
        return new SharestoneSelectionEntry(waystone.getWaystoneUid(), waystone.getName(), distance, waystone.getWaystoneType(),
                waystone.getVisibility(), waystone.getOrigin(), waystone.getDimension());
    }

    public RestrictedWaystone toRestrictedWaystone() {
        return new RestrictedWaystone(waystoneType, id, origin, dimension, name, visibility, distanceMeters);
    }

    private static void write(RegistryFriendlyByteBuf buf, SharestoneSelectionEntry entry) {
        buf.writeUUID(entry.id());
        ComponentSerialization.STREAM_CODEC.encode(buf, entry.name());
        buf.writeInt(entry.distanceMeters());
        buf.writeResourceLocation(entry.waystoneType());
        buf.writeEnum(entry.visibility());
        buf.writeEnum(entry.origin());
        buf.writeResourceLocation(entry.dimension().location());
    }

    private static SharestoneSelectionEntry read(RegistryFriendlyByteBuf buf) {
        final var id = buf.readUUID();
        final var name = ComponentSerialization.STREAM_CODEC.decode(buf);
        final var distance = buf.readInt();
        final var waystoneType = buf.readResourceLocation();
        final var visibility = buf.readEnum(WaystoneVisibility.class);
        final var origin = buf.readEnum(WaystoneOrigin.class);
        final var dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        return new SharestoneSelectionEntry(id, name, distance, waystoneType, visibility, origin, dimension);
    }
}
