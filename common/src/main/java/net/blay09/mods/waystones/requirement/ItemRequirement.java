package net.blay09.mods.waystones.requirement;

import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRequirement implements WarpRequirement {

    private ItemStack itemStack;
    private int count;

    public ItemRequirement(ItemStack item, int count) {
        this.itemStack = item;
        this.count = count;
    }

    @Override
    public boolean canAfford(Player player) {
        return InventoryItemResolver.countMatchingInPlayerInventory(player, this::matchesItem) >= this.count;
    }

    @Override
    public void consume(Player player) {
        InventoryItemResolver.consumeFromPlayerInventory(player, this::matchesItem, this.count);
    }

    @Override
    public void rollback(Player player) {
        var added = 0;
        while(added < count) {
            final var leftToAdd = count - added;
            final var itemStack = this.itemStack.copy();
            itemStack.setCount(Math.min(itemStack.getMaxStackSize(), leftToAdd));
            if (!player.addItem(itemStack)) {
                player.drop(itemStack, false, false);
            }
            added += itemStack.getCount();
        }
    }

    @Override
    public void appendHoverText(Player player, List<Component> tooltip) {
        if (count > 0) {
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.item_requirement", count, itemStack.getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty() || count <= 0;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private boolean matchesItem(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(itemStack, stack);
    }
}
