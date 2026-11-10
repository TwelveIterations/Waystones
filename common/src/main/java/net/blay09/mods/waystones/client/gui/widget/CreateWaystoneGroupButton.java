package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.core.GroupValidation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CreateWaystoneGroupButton extends Button.Plain {

    private static final Component TEXT = Component.literal("+");
    private static final Component LABEL = Component.translatable("gui.waystones.manage_groups.create");

    public CreateWaystoneGroupButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, TEXT, pressable, _ -> LABEL.copy());
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        if (isHovered) {
            final var tooltip = active
                    ? LABEL
                    : Component.translatable("gui.waystones.manage_groups.limit_reached", GroupValidation.MAX_CUSTOM_GROUPS).withStyle(ChatFormatting.RED);
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY, null);
        }
    }
}
