package net.blay09.mods.waystones.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundWarpPlateEjectEffectPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClientboundWarpPlateEjectEffectPacket> TYPE = new Type<>(id("warp_plate_eject_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWarpPlateEjectEffectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundWarpPlateEjectEffectPacket::pos,
            ClientboundWarpPlateEjectEffectPacket::new
    );

    public static void handle(Player player, ClientboundWarpPlateEjectEffectPacket message) {
        Level level = player.level();
        if (level != null) {
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.SMALL_GUST, message.pos.getX() + 0.5 + (level.random.nextDouble() - 0.5), message.pos.getY() + level.random.nextDouble(), message.pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5), (level.random.nextDouble() - 0.5) * 2, -level.random.nextDouble(), (level.random.nextDouble() - 0.5) * 2);
                // TODO sound
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
