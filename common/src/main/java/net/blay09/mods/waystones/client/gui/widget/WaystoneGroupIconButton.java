package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.client.WaystoneGroupIcons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WaystoneGroupIconButton extends Button {

    private ResourceLocation icon;

    public WaystoneGroupIconButton(int x, int y, ResourceLocation icon) {
        super(x, y, 21, 21, Component.empty(), button -> ((WaystoneGroupIconButton) button).cycleIcon(), Button.DEFAULT_NARRATION);
        this.icon = icon;
        updateTooltip();
    }

    public ResourceLocation getIcon() {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && active && visible && isMouseOver(mouseX, mouseY)) {
            cycleIconBackwards();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blitSprite(icon, getX() + 3, getY() + 3, 15, 15);
    }
}
