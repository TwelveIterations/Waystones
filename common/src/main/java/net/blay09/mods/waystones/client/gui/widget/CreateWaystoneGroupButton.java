package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.core.GroupValidation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class CreateWaystoneGroupButton extends Button {

    private static final Component TEXT = Component.literal("+");
    private static final Component LABEL = Component.translatable("gui.waystones.manage_groups.create");
    private static final Tooltip CREATE_TOOLTIP = Tooltip.create(LABEL);
    private static final Tooltip LIMIT_TOOLTIP = Tooltip.create(Component.translatable("gui.waystones.manage_groups.limit_reached",
            GroupValidation.MAX_CUSTOM_GROUPS).withStyle(ChatFormatting.RED));

    public CreateWaystoneGroupButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, TEXT, pressable, Button.DEFAULT_NARRATION);
        setTooltip(CREATE_TOOLTIP);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        setTooltip(active ? CREATE_TOOLTIP : LIMIT_TOOLTIP);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }
}
