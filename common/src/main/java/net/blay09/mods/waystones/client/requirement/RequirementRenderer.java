package net.blay09.mods.waystones.client.requirement;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface RequirementRenderer<T> {
    void renderWidget(Player player, T requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int x, int y);

    /**
     * @deprecated Use {@link #getWidth(Player, T)} instead.
     */
    @Deprecated
    default int getWidth(T requirement) {
        return 16;
    }

    default int getWidth(Player player, T requirement) {
        return getWidth(requirement);
    }

    default int getOrder() {
        return 100;
    }

    default void appendHoverText(Player player, T requirement, List<Component> tooltip) {
    }
}
