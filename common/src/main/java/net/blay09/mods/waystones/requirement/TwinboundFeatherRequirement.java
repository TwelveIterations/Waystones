package net.blay09.mods.waystones.requirement;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TwinboundFeatherRequirement() {
    public static final TwinboundFeatherRequirement INSTANCE = new TwinboundFeatherRequirement();
    public static final StreamCodec<RegistryFriendlyByteBuf, TwinboundFeatherRequirement> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}
