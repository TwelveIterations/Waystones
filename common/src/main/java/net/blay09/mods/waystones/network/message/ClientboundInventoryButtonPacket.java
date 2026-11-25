package net.blay09.mods.waystones.network.message;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.shogi.network.ShogiStreamCodecs;
import net.blay09.mods.waystones.client.InventoryButtonGuiHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.blay09.mods.waystones.Waystones.id;

public record ClientboundInventoryButtonPacket(Either<List<Object>, List<Object>> requirements) implements CustomPacketPayload {
    public static final Type<ClientboundInventoryButtonPacket> TYPE = new Type<>(id("inventory_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundInventoryButtonPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.either(ShogiStreamCodecs.LIST_STREAM_CODEC, ShogiStreamCodecs.LIST_STREAM_CODEC),
            ClientboundInventoryButtonPacket::requirements,
            ClientboundInventoryButtonPacket::new
    );

    public static void handle(Player player, ClientboundInventoryButtonPacket message) {
        InventoryButtonGuiHandler.updateRequirements(message.requirements);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
