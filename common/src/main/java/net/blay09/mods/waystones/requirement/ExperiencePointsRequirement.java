package net.blay09.mods.waystones.requirement;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ExperiencePointsRequirement(long available, int required) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ExperiencePointsRequirement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            ExperiencePointsRequirement::available,
            ByteBufCodecs.VAR_INT,
            ExperiencePointsRequirement::required,
            ExperiencePointsRequirement::new
    );
}
