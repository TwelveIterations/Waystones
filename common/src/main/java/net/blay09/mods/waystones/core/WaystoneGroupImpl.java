package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record WaystoneGroupImpl(ResourceLocation identifier, Component name, ResourceLocation icon, int color, boolean inbuilt, boolean hidden, int sortIndex) implements WaystoneGroup {
    public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneGroup> STREAM_CODEC = StreamCodec.of(WaystoneGroupImpl::write, WaystoneGroupImpl::read);

    public static WaystoneGroup read(RegistryFriendlyByteBuf buf) {
        final var identifier = buf.readResourceLocation();
        final var name = ComponentSerialization.STREAM_CODEC.decode(buf);
        final var icon = buf.readResourceLocation();
        final var color = buf.readInt();
        final var inbuilt = buf.readBoolean();
        final var hidden = buf.readBoolean();
        final var sortIndex = buf.readInt();
        return new WaystoneGroupImpl(identifier, name, icon, color, inbuilt, hidden, sortIndex);
    }

    public static void write(RegistryFriendlyByteBuf buf, WaystoneGroup group) {
        buf.writeResourceLocation(group.identifier());
        ComponentSerialization.STREAM_CODEC.encode(buf, group.name());
        buf.writeResourceLocation(group.icon());
        buf.writeInt(group.color());
        buf.writeBoolean(group.inbuilt());
        buf.writeBoolean(group.hidden());
        buf.writeInt(group.sortIndex());
    }
}
