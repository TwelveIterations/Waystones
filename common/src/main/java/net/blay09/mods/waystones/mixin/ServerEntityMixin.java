package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.WaystoneTeleportedEntity;
import net.blay09.mods.waystones.network.message.ClientboundEntityPositionSyncMessage;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @ModifyArg(method = "sendChanges", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private Object replaceTeleportEntityPacket(Object packet) {
        if (packet instanceof ClientboundTeleportEntityPacket) {
            if (WaystonesConfig.getActive().compatibility.fixVanillaTeleportBug
                    || ((WaystoneTeleportedEntity) entity).waystones$consumeTeleportedByWaystone()) {
                return new ClientboundCustomPayloadPacket(new ClientboundEntityPositionSyncMessage(entity));
            }
        }

        return packet;
    }

}
