package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.UUID;
import java.util.function.Consumer;

public record BoundScrollComponent(UUID waystoneId, Component waystoneName) implements TooltipProvider {
    public static final Codec<BoundScrollComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(BoundScrollComponent::waystoneId),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(BoundScrollComponent::waystoneName)
    ).apply(instance, BoundScrollComponent::new));

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        final var boundToValueComponent = waystoneName.copy().withStyle(ChatFormatting.AQUA);
        tooltip.accept(Component.translatable("tooltip.waystones.bound_to", boundToValueComponent).withStyle(ChatFormatting.GRAY));
    }
}
