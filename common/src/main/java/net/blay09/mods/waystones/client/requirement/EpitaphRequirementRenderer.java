package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.requirement.EpitaphRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EpitaphRequirementRenderer implements RequirementRenderer<EpitaphRequirement> {
    @Override
    public void renderWidget(Player player, EpitaphRequirement requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.item(new ItemStack(ModItems.epitaph.asItem()), x, y);
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public void appendHoverText(Player player, EpitaphRequirement requirement, List<Component> tooltip) {
        tooltip.add(Component.translatable("gui.waystones.waystone_selection.epitaph_requirement",
                new ItemStack(ModItems.epitaph.asItem()).getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
