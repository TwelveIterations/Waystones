package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRequirementRenderer implements RequirementRenderer<ItemCostInformation> {
    @Override
    public void renderWidget(Player player, ItemCostInformation requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        final var font = Minecraft.getInstance().font;
        final var itemStack = requirement.item().stream().findFirst().map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
        guiGraphics.renderItem(itemStack, x, y);
        guiGraphics.renderItemDecorations(font, itemStack, x, y, requirement.required() > 1 ? String.valueOf(requirement.required()) : null);
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void appendHoverText(Player player, ItemCostInformation requirement, List<Component> tooltip) {
        if (requirement.required() > 0) {
            final var itemStack = requirement.item().stream().findFirst().map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
            tooltip.add(Component.translatable("gui.waystones.waystone_selection.item_requirement", requirement.required(), itemStack.getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}
