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

public record WaystoneReferenceComponent(UUID waystoneId, Component waystoneName) implements TooltipProvider {
    public static final Codec<WaystoneReferenceComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(WaystoneReferenceComponent::waystoneId),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(WaystoneReferenceComponent::waystoneName)
    ).apply(instance, WaystoneReferenceComponent::new));

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        tooltip.accept(waystoneName.copy().withStyle(ChatFormatting.AQUA));
    }
}
