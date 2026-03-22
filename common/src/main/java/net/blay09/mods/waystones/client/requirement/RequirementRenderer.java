package net.blay09.mods.waystones.client.requirement;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface RequirementRenderer<T> {
    void renderWidget(Player player, T requirement, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y);

    int getWidth(Player player, T requirement);

    default int getOrder() {
        return 100;
    }

    default void appendHoverText(Player player, T requirement, List<Component> tooltip) {
    }
}
