package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;

import static net.blay09.mods.waystones.Waystones.id;

public class ToggleWaystoneHiddenButton extends Button {

    private static final Identifier VISIBLE_SPRITE = id("widgets/shown");
    private static final Identifier HIDDEN_SPRITE = id("widgets/hidden");

    private final BooleanSupplier hidden;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private boolean lastHidden;

    public ToggleWaystoneHiddenButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, BooleanSupplier hidden, OnPress pressable) {
        super(x, y, 18, 18, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.hidden = hidden;
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        lastHidden = hidden.getAsBoolean();
        updateTooltip();
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(Component.translatable(lastHidden
                ? "gui.waystones.manage_waystones.show_waystone"
                : "gui.waystones.manage_waystones.hide_waystone")));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        final var currentHidden = hidden.getAsBoolean();
        if (currentHidden != lastHidden) {
            lastHidden = currentHidden;
            updateTooltip();
        }

        if (getBottom() > visibleRegionStart && getY() < visibleRegionStart + visibleRegionHeight) {
            final var sprite = currentHidden ? HIDDEN_SPRITE : VISIBLE_SPRITE;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), 13, 13, isHovered ? 0xFFFFFFFF : 0xBFBFBFBF);
        }
    }
}
