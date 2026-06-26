package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.blay09.mods.waystones.Waystones.id;

public class ToggleWaystoneGroupHiddenButton extends Button {

    private static final ResourceLocation VISIBLE_SPRITE = id("widgets/shown");
    private static final ResourceLocation HIDDEN_SPRITE = id("widgets/hidden");

    private final boolean hidden;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;

    public ToggleWaystoneGroupHiddenButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, boolean hidden, OnPress pressable) {
        super(x, y, 18, 18, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.hidden = hidden;
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        updateTooltip();
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(Component.translatable(hidden
                ? "gui.waystones.manage_groups.show_group"
                : "gui.waystones.manage_groups.hide_group")));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            final var sprite = hidden ? HIDDEN_SPRITE : VISIBLE_SPRITE;
            final var alpha = isHovered ? 1f : 0.75f;
            guiGraphics.setColor(alpha, alpha, alpha, 1f);
            guiGraphics.blitSprite(sprite, getX(), getY(), 13, 13);
            guiGraphics.setColor(1f, 1f, 1f, 1f);
        }
    }
}
