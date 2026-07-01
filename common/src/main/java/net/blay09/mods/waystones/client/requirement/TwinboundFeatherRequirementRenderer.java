package net.blay09.mods.waystones.client.requirement;

import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.requirement.TwinboundFeatherRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TwinboundFeatherRequirementRenderer implements RequirementRenderer<TwinboundFeatherRequirement> {
    @Override
    public void renderWidget(Player player, TwinboundFeatherRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y) {
        guiGraphics.renderItem(new ItemStack(ModItems.twinboundFeather), x, y);
    }

    @Override
    public int getOrder() {
        return 5;
    }

}
