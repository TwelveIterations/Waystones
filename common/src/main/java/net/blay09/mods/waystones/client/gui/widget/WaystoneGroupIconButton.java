package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class WaystoneGroupIconButton extends Button {

    private Identifier icon;

    public WaystoneGroupIconButton(int x, int y, Identifier icon) {
        super(x, y, 21, 21, Component.empty(), button -> ((WaystoneGroupIconButton) button).cycleIcon(), Button.DEFAULT_NARRATION);
        this.icon = icon;
        updateTooltip();
    }

    public Identifier getIcon() {
        return icon;
    }

    private void cycleIcon() {
        final var presetIcons = WaystoneGroupIcons.getPresetIcons();
        final var currentIndex = presetIcons.indexOf(icon);
        icon = presetIcons.get((currentIndex + 1) % presetIcons.size());
        updateTooltip();
    }

    private void cycleIconBackwards() {
        final var presetIcons = WaystoneGroupIcons.getPresetIcons();
        final var currentIndex = presetIcons.indexOf(icon);
        icon = presetIcons.get(currentIndex <= 0 ? presetIcons.size() - 1 : currentIndex - 1);
        updateTooltip();
    }

    private void updateTooltip() {
        final var iconName = Component.translatable("waystones." + icon.getPath().replace('/', '.'));
        setTooltip(Tooltip.create(Component.translatable("gui.waystones.group_settings.icon", iconName)));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && active && visible && isMouseOver(event.x(), event.y())) {
            cycleIconBackwards();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(guiGraphics);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, getX() + 3, getY() + 3, 15, 15);
    }
}
