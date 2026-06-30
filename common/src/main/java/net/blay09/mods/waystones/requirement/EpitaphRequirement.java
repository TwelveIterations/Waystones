package net.blay09.mods.waystones.requirement;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EpitaphRequirement() {
    public static final EpitaphRequirement INSTANCE = new EpitaphRequirement();
    public static final StreamCodec<RegistryFriendlyByteBuf, EpitaphRequirement> STREAM_CODEC = StreamCodec.unit(INSTANCE);
}
