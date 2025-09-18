package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.blay09.mods.waystones.component.BlankScrollComponent;
import net.blay09.mods.waystones.component.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Consumer;

public class BlankScrollItem extends Item {
    public BlankScrollItem(Properties properties) {
        super(properties.stacksTo(64).component(ModComponents.blankScroll.get(), BlankScrollComponent.INSTANCE));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            final var blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
            if (blockEntity instanceof WaystoneBlockEntityBase waystoneBlockEntityBase) {
                final var waystone = waystoneBlockEntityBase.getWaystone();
                final var boundScrollStack = new ItemStack(ModItems.boundScroll);
                WaystonesAPI.setBoundWaystone(boundScrollStack, waystone);
                final var player = context.getPlayer();
                int emptySlot = player.getInventory().getFreeSlot();
                int stackableSlot = player.getInventory().getSlotWithRemainingSpace(boundScrollStack);
                if ((emptySlot != -1 || stackableSlot != -1) || (!player.hasInfiniteMaterials() && context.getItemInHand().getCount() == 1)) {
                    context.getItemInHand().consume(1, player);
                    if (!player.addItem(boundScrollStack)) {
                        player.drop(boundScrollStack, false);
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
        itemStack.addToTooltip(ModComponents.blankScroll.get(), context, display, list, flag);
    }
}
