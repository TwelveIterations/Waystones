package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum WaystoneOrigin implements StringRepresentable {
    UNKNOWN,
    WILDERNESS,
    DUNGEON,
    VILLAGE,
    PLAYER;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    private static final IntFunction<WaystoneOrigin> BY_ID = ByIdMap.continuous(WaystoneOrigin::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final Codec<WaystoneOrigin> CODEC = Codec.withAlternative(StringRepresentable.fromEnum(WaystoneOrigin::values), Codec.STRING,
            WaystoneOrigin::valueOf);
    public static final StreamCodec<ByteBuf, WaystoneOrigin> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WaystoneOrigin::ordinal);
}
