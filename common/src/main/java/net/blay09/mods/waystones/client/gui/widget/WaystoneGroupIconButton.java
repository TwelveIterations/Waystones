package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneGroups;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.List;

public class WaystoneGroupIconButton extends Button {

    private static final List<Identifier> PRESET_ICONS = WaystoneGroups.PRESET_ICONS;

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
        final var currentIndex = PRESET_ICONS.indexOf(icon);
        icon = PRESET_ICONS.get((currentIndex + 1) % PRESET_ICONS.size());
        updateTooltip();
    }

    private void cycleIconBackwards() {
        final var currentIndex = PRESET_ICONS.indexOf(icon);
        icon = PRESET_ICONS.get(currentIndex <= 0 ? PRESET_ICONS.size() - 1 : currentIndex - 1);
        updateTooltip();
    }

    private void updateTooltip() {
        final var iconName = Component.translatable("waystones." + icon.getPath().replace('/', '.'));
        setTooltip(Tooltip.create(Component.translatable("gui.waystones.group_settings.icon", iconName)));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1 && active && visible && isMouseOver(event.x(), event.y())) {
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
