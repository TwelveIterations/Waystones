package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.function.IntFunction;

import java.util.Locale;

public enum WaystoneVisibility implements StringRepresentable {
    ACTIVATION,
    GLOBAL,
    SHARD_ONLY,
    SHARESTONES;

    private static final IntFunction<WaystoneVisibility> BY_ID = ByIdMap.continuous(WaystoneVisibility::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, WaystoneVisibility> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WaystoneVisibility::ordinal);
    public static final StreamCodec<ByteBuf, List<WaystoneVisibility>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
    public static final Codec<WaystoneVisibility> CODEC = StringRepresentable.fromEnum(WaystoneVisibility::values);

    public static WaystoneVisibility getDefaultForWaystoneKind(Identifier kind) {
        if (WaystoneKinds.isSharestone(kind)) {
            return WaystoneVisibility.SHARESTONES;
        } else if (kind.equals(WaystoneKinds.WARP_PLATE)) {
            return WaystoneVisibility.SHARD_ONLY;
        } else {
            return WaystoneVisibility.ACTIVATION;
        }
    }

    public boolean isSupportedForWaystoneKind(Identifier kind) {
        return switch(this) {
            case ACTIVATION, GLOBAL -> WaystoneKinds.WAYSTONE.equals(kind);
            case SHARD_ONLY -> WaystoneKinds.WARP_PLATE.equals(kind);
            case SHARESTONES -> WaystoneKinds.isSharestone(kind);
        };
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
