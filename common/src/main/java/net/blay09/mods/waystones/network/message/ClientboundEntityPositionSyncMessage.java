package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.mixin.ClientLevelAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientboundEntityPositionSyncMessage implements CustomPacketPayload {

    public static final Type<ClientboundEntityPositionSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID,
            "entity_position_sync"));
    private static final double SNAP_DISTANCE_SQUARED = 4096.0;

    private final int entityId;
    private final Vec3 position;
    private final Vec3 deltaMovement;
    private final float yRot;
    private final float xRot;
    private final boolean onGround;

    public ClientboundEntityPositionSyncMessage(Entity entity) {
        this(entity.getId(), entity.trackingPosition(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), entity.onGround());
    }

    public ClientboundEntityPositionSyncMessage(int entityId, Vec3 position, Vec3 deltaMovement, float yRot, float xRot, boolean onGround) {
        this.entityId = entityId;
        this.position = position;
        this.deltaMovement = deltaMovement;
        this.yRot = yRot;
        this.xRot = xRot;
        this.onGround = onGround;
    }

    public static void encode(FriendlyByteBuf buf, ClientboundEntityPositionSyncMessage message) {
        buf.writeVarInt(message.entityId);
        writeVec3(buf, message.position);
        writeVec3(buf, message.deltaMovement);
        buf.writeFloat(message.yRot);
        buf.writeFloat(message.xRot);
        buf.writeBoolean(message.onGround);
    }

    public static ClientboundEntityPositionSyncMessage decode(FriendlyByteBuf buf) {
        return new ClientboundEntityPositionSyncMessage(buf.readVarInt(), readVec3(buf), readVec3(buf), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }

    public static void handle(Player player, ClientboundEntityPositionSyncMessage message) {
        final var entity = player.level().getEntity(message.entityId);
        if (entity == null) {
            return;
        }

        entity.syncPacketPositionCodec(message.position.x, message.position.y, message.position.z);
        entity.setDeltaMovement(message.deltaMovement);

        if (!entity.isControlledByLocalInstance()) {
            if (shouldSnap(player, entity, message.position)) {
                entity.moveTo(message.position, message.yRot, message.xRot);
                if (entity.hasIndirectPassenger(player)) {
                    entity.positionRider(player);
                    player.setOldPosAndRot();
                }
            } else {
                entity.lerpTo(message.position.x, message.position.y, message.position.z, message.yRot, message.xRot, 3);
            }

            entity.setOnGround(message.onGround);
        }
    }

    private static boolean shouldSnap(Player player, Entity entity, Vec3 position) {
        return entity.position().distanceToSqr(position) > SNAP_DISTANCE_SQUARED || !isTicking(player, entity);
    }

    private static boolean isTicking(Player player, Entity entity) {
        if (player.level() instanceof ClientLevel clientLevel) {
            return ((ClientLevelAccessor) clientLevel).waystones$getTickingEntities().contains(entity);
        }

        return true;
    }

    private static void writeVec3(FriendlyByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    private static Vec3 readVec3(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
