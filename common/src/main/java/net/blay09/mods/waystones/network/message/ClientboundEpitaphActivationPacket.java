package net.blay09.mods.waystones.network.message;

import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.blay09.mods.waystones.Waystones.id;

public class ClientboundEpitaphActivationPacket implements CustomPacketPayload {

    public static final ClientboundEpitaphActivationPacket INSTANCE = new ClientboundEpitaphActivationPacket();
    public static final Type<ClientboundEpitaphActivationPacket> TYPE = new Type<>(id("epitaph_activation"));

    private ClientboundEpitaphActivationPacket() {
    }

    public static void encode(FriendlyByteBuf buf, ClientboundEpitaphActivationPacket message) {
    }

    public static ClientboundEpitaphActivationPacket decode(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    public static void handle(Player player, ClientboundEpitaphActivationPacket message) {
        final var minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.TOTEM_USE, 1f, 1f));
        minecraft.gameRenderer.displayItemActivation(new ItemStack(ModItems.epitaph));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
