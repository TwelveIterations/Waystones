package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.shogi.common.effect.cost.ItemCostInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRequirementRenderer implements RequirementRenderer<ItemCostInformation> {
    @Override
    public void renderWidget(Player player, ItemCostInformation requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        if (requirement.required() > 0) {
            final var font = Minecraft.getInstance().font;
            final var itemStack = requirement.item().stream().findFirst().map(Holder::value).map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
            guiGraphics.item(itemStack, x, y);
            guiGraphics.itemDecorations(font, itemStack, x, y, requirement.required() > 1 ? String.valueOf(requirement.required()) : null);
        }
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
