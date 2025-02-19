package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface ITooltipProvider {
    boolean shouldShowTooltip();

    List<Component> getTooltipComponents();
}
