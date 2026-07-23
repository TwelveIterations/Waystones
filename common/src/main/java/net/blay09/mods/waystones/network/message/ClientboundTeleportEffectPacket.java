package net.blay09.mods.waystones.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundTeleportEffectPacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundTeleportEffectPacket> TYPE = new CustomPacketPayload.Type<>(id("teleport_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTeleportEffectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundTeleportEffectPacket::pos,
            ClientboundTeleportEffectPacket::new
    );

    public static void handle(Player player, ClientboundTeleportEffectPacket message) {
        Level level = player.level();
        if (level != null) {
            final var random = level.getRandom();
            for (int i = 0; i < 128; i++) {
                level.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (random.nextDouble() - 0.5) * 3, message.pos.getY() + random.nextDouble() * 3, message.pos.getZ() + (random.nextDouble() - 0.5) * 3, (random.nextDouble() - 0.5) * 2, -random.nextDouble(), (random.nextDouble() - 0.5) * 2);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
