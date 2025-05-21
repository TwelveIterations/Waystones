package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record ReturnScrollComponent() implements TooltipProvider {
    public static final ReturnScrollComponent INSTANCE = new ReturnScrollComponent();
    public static final Codec<ReturnScrollComponent> CODEC = Codec.unit(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        final var player = Balm.getProxy().getClientPlayer();
        if (player != null) {
            final var nearestWaystone = PlayerWaystoneManager.getNearestWaystone(player);
            tooltip.accept(nearestWaystone.map(it -> it.getName().copy().withStyle(ChatFormatting.DARK_AQUA))
                    .map(it -> Component.translatable("tooltip.waystones.bound_to", it).withStyle(ChatFormatting.GRAY))
                    .orElseGet(() -> Component.translatable("gui.waystones.inventory.no_waystones_activated").withStyle(ChatFormatting.RED)));
        }
    }
}
