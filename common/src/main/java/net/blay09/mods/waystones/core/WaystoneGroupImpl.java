package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.WaystoneGroup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record WaystoneGroupImpl(ResourceLocation identifier, ResourceLocation icon, int color, boolean inbuilt) implements WaystoneGroup {
    public static final StreamCodec<RegistryFriendlyByteBuf, WaystoneGroup> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            WaystoneGroup::identifier,
            ResourceLocation.STREAM_CODEC,
            WaystoneGroup::icon,
            ByteBufCodecs.INT,
            WaystoneGroup::color,
            ByteBufCodecs.BOOL,
            WaystoneGroup::inbuilt,
            WaystoneGroupImpl::new
    );
}
