package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

import static net.blay09.mods.waystones.Waystones.id;

public class SortWaystonesButton extends Button {

    public enum Mode {
        MANUAL("manual"),
        NAME("name"),
        DISTANCE("distance");

        private final String translationSuffix;
        private final ResourceLocation icon;

        Mode(String translationSuffix) {
            this.translationSuffix = translationSuffix;
            this.icon = id("waystone_selection/sort_" + translationSuffix);
        }

        public Mode next() {
            final var modes = values();
            return modes[(ordinal() + 1) % modes.length];
        }

        public Component label() {
            return Component.translatable("gui.waystones.waystone_selection.sort." + translationSuffix);
        }
    }

    private final Consumer<Mode> onModeChanged;
    private Mode mode;

    public SortWaystonesButton(int x, int y, Mode mode, Consumer<Mode> onModeChanged) {
        super(x, y, 20, 20, Component.empty(), button -> {
        }, Button.DEFAULT_NARRATION);
        this.mode = mode;
        this.onModeChanged = onModeChanged;
        updateTooltip();
    }

    @Override
    public void onPress() {
        mode = mode.next();
        updateTooltip();
        onModeChanged.accept(mode);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(mode.icon, getX() + 2, getY() + 2, 16, 16);
    }

    private void updateTooltip() {
        setTooltip(Tooltip.create(Component.translatable("gui.waystones.waystone_selection.sort", mode.label())));
    }
}
