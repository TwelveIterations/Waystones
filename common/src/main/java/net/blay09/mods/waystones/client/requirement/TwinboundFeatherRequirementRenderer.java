package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.requirement.TwinboundFeatherRequirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TwinboundFeatherRequirementRenderer implements RequirementRenderer<TwinboundFeatherRequirement> {
    @Override
    public void renderWidget(Player player, TwinboundFeatherRequirement requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.item(new ItemStack(ModItems.twinboundFeather.asItem()), x, y);
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public void appendHoverText(Player player, TwinboundFeatherRequirement requirement, List<Component> tooltip) {
        tooltip.add(Component.translatable("gui.waystones.waystone_selection.twinbound_feather_requirement",
                new ItemStack(ModItems.twinboundFeather.asItem()).getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
