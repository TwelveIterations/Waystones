package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class CreateWaystoneGroupButton extends Button {

    private static final Component TEXT = Component.literal("+");
    private static final Component LABEL = Component.translatable("gui.waystones.manage_groups.create");

    public CreateWaystoneGroupButton(int x, int y, OnPress pressable) {
        super(x, y, 20, 20, TEXT, pressable, Button.DEFAULT_NARRATION);
        setTooltip(Tooltip.create(LABEL));
    }
}
