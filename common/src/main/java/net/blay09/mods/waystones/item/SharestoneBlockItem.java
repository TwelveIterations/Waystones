package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.component.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class SharestoneBlockItem extends BlockItem {
    public SharestoneBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        itemStack.addToTooltip(ModComponents.description.get(), context, display, list, flag);
    }
}
