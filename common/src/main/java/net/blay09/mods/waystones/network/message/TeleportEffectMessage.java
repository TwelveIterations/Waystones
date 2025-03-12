package net.blay09.mods.waystones.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.blay09.mods.waystones.Waystones.id;

public record TeleportEffectMessage(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TeleportEffectMessage> TYPE = new CustomPacketPayload.Type<>(id("teleport_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportEffectMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            TeleportEffectMessage::pos,
            TeleportEffectMessage::new
    );

    public static void handle(Player player, TeleportEffectMessage message) {
        Level level = player.level();
        if (level != null) {
            for (int i = 0; i < 128; i++) {
                level.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (level.random.nextDouble() - 0.5) * 3, message.pos.getY() + level.random.nextDouble() * 3, message.pos.getZ() + (level.random.nextDouble() - 0.5) * 3, (level.random.nextDouble() - 0.5) * 2, -level.random.nextDouble(), (level.random.nextDouble() - 0.5) * 2);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
