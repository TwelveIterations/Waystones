package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record WaystoneGroupImpl(Identifier identifier, Component name, Identifier icon, int color, boolean inbuilt, boolean hidden) implements WaystoneGroup {
    public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneGroup> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            WaystoneGroup::identifier,
            ComponentSerialization.STREAM_CODEC,
            WaystoneGroup::name,
            Identifier.STREAM_CODEC,
            WaystoneGroup::icon,
            ByteBufCodecs.INT,
            WaystoneGroup::color,
            ByteBufCodecs.BOOL,
            WaystoneGroup::inbuilt,
            ByteBufCodecs.BOOL,
            WaystoneGroup::hidden,
            WaystoneGroupImpl::new
    );
}
