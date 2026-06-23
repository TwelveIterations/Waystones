package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

import static net.blay09.mods.waystones.Waystones.id;

public class SortWaystonesButton extends Button.Plain {

    public enum Mode {
        MANUAL("manual"),
        NAME("name"),
        DISTANCE("distance");

        private final String translationSuffix;
        private final Identifier icon;

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
        super(x, y, 20, 20, Component.empty(), _ -> {
        }, Button.DEFAULT_NARRATION);
        this.mode = mode;
        this.onModeChanged = onModeChanged;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        mode = mode.next();
        onModeChanged.accept(mode);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, mode.icon, getX() + 2, getY() + 2, 16, 16);
        if (isHovered) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                    Component.translatable("gui.waystones.waystone_selection.sort", mode.label()),
                    mouseX,
                    mouseY,
                    null);
        }
    }
}
