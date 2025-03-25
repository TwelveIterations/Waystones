package net.blay09.mods.waystones.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum WaystoneVisibility implements StringRepresentable {
    ACTIVATION,
    GLOBAL,
    SHARD_ONLY,
    ORANGE_SHARESTONE,
    MAGENTA_SHARESTONE,
    LIGHT_BLUE_SHARESTONE,
    YELLOW_SHARESTONE,
    LIME_SHARESTONE,
    PINK_SHARESTONE,
    GRAY_SHARESTONE,
    LIGHT_GRAY_SHARESTONE,
    CYAN_SHARESTONE,
    PURPLE_SHARESTONE,
    BLUE_SHARESTONE,
    BROWN_SHARESTONE,
    GREEN_SHARESTONE,
    RED_SHARESTONE,
    BLACK_SHARESTONE;

    @Override
    public String getSerializedName() {
        return name();
    }

    public static WaystoneVisibility fromWaystoneType(ResourceLocation waystoneType) {
        if (WaystoneTypes.isSharestone(waystoneType)) {
            return switch (waystoneType.getPath()) {
                case "orange_sharestone" -> WaystoneVisibility.ORANGE_SHARESTONE;
                case "magenta_sharestone" -> WaystoneVisibility.MAGENTA_SHARESTONE;
                case "light_blue_sharestone" -> WaystoneVisibility.LIGHT_BLUE_SHARESTONE;
                case "yellow_sharestone" -> WaystoneVisibility.YELLOW_SHARESTONE;
                case "lime_sharestone" -> WaystoneVisibility.LIME_SHARESTONE;
                case "pink_sharestone" -> WaystoneVisibility.PINK_SHARESTONE;
                case "gray_sharestone" -> WaystoneVisibility.GRAY_SHARESTONE;
                case "light_gray_sharestone" -> WaystoneVisibility.LIGHT_GRAY_SHARESTONE;
                case "cyan_sharestone" -> WaystoneVisibility.CYAN_SHARESTONE;
                case "purple_sharestone" -> WaystoneVisibility.PURPLE_SHARESTONE;
                case "blue_sharestone" -> WaystoneVisibility.BLUE_SHARESTONE;
                case "brown_sharestone" -> WaystoneVisibility.BROWN_SHARESTONE;
                case "green_sharestone" -> WaystoneVisibility.GREEN_SHARESTONE;
                case "red_sharestone" -> WaystoneVisibility.RED_SHARESTONE;
                case "black_sharestone" -> WaystoneVisibility.BLACK_SHARESTONE;
                default -> WaystoneVisibility.ACTIVATION;
            };
        } else if (waystoneType.equals(WaystoneTypes.WARP_PLATE)) {
            return WaystoneVisibility.SHARD_ONLY;
        } else {
            return WaystoneVisibility.ACTIVATION;
        }
    }

    private static final IntFunction<WaystoneVisibility> BY_ID = ByIdMap.continuous(WaystoneVisibility::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static Codec<WaystoneVisibility> CODEC = StringRepresentable.fromEnum(WaystoneVisibility::values);
    public static final StreamCodec<ByteBuf, WaystoneVisibility> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WaystoneVisibility::ordinal);
}
