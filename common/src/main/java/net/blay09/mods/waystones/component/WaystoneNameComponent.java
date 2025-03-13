package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record WaystoneNameComponent(Component component) implements TooltipProvider {
    public static final Codec<WaystoneNameComponent> CODEC = ComponentSerialization.CODEC.xmap(WaystoneNameComponent::new, WaystoneNameComponent::component);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        tooltip.accept(component.copy().withStyle(ChatFormatting.AQUA));
    }
}
