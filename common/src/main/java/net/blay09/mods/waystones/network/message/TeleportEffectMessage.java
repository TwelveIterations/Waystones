package net.blay09.mods.waystones.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeleportEffectMessage {

    private final BlockPos pos;

    public TeleportEffectMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(TeleportEffectMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static TeleportEffectMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new TeleportEffectMessage(pos);
    }

    public static void handle(Player player, TeleportEffectMessage message) {
        Level level = player.level();
        if (level != null) {
            for (int i = 0; i < 128; i++) {
                level.addParticle(ParticleTypes.PORTAL, message.pos.getX() + (level.random.nextDouble() - 0.5) * 3, message.pos.getY() + level.random.nextDouble() * 3, message.pos.getZ() + (level.random.nextDouble() - 0.5) * 3, (level.random.nextDouble() - 0.5) * 2, -level.random.nextDouble(), (level.random.nextDouble() - 0.5) * 2);
            }
        }
    }
}
