package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * 玩家信息 - 用于网络序列化的玩家数据
 */
public record PlayerInfo(
        UUID uuid,
        String name,
        ResourceKey<Level> dimension,
        BlockPos position
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInfo> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                UUIDUtil.STREAM_CODEC.encode(buf, value.uuid);
                buf.writeUtf(value.name);
                ResourceKey.streamCodec(Registries.DIMENSION).encode(buf, value.dimension);
                BlockPos.STREAM_CODEC.encode(buf, value.position);
            },
            buf -> new PlayerInfo(
                    UUIDUtil.STREAM_CODEC.decode(buf),
                    buf.readUtf(),
                    ResourceKey.streamCodec(Registries.DIMENSION).decode(buf),
                    BlockPos.STREAM_CODEC.decode(buf)
            )
    );

    /**
     * 获取玩家名称作为组件
     */
    public Component getDisplayName() {
        return Component.literal(name);
    }
}
