package net.blay09.mods.waystones.requirement;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record CooldownRequirement(Identifier identifier, long remainingTicks, int requestedTicks, long nowUnixMs, long nanosecondsPerTick) {
    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownRequirement> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            CooldownRequirement::identifier,
            ByteBufCodecs.VAR_LONG,
            CooldownRequirement::remainingTicks,
            ByteBufCodecs.VAR_INT,
            CooldownRequirement::requestedTicks,
            ByteBufCodecs.VAR_LONG,
            CooldownRequirement::nowUnixMs,
            ByteBufCodecs.LONG,
            CooldownRequirement::nanosecondsPerTick,
            CooldownRequirement::new
    );
}
