package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record BlankScrollComponent() implements TooltipProvider {
    public static final BlankScrollComponent INSTANCE = new BlankScrollComponent();
    public static final Codec<BlankScrollComponent> CODEC = MapCodec.unitCodec(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        tooltip.accept(Component.translatable("tooltip.waystones.blank_scroll").withStyle(ChatFormatting.GRAY));
    }
}
