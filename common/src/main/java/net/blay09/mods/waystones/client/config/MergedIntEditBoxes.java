package net.blay09.mods.waystones.client.config;

import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreenContext;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreenEditBox;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreenRowState;
import net.blay09.mods.balm.platform.config.schema.ConfiguredProperty;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

final class MergedIntEditBoxes extends AbstractContainerWidget {
    private static final int GAP = 8;

    private final BalmConfigScreenEditBox<Integer> first;
    private final BalmConfigScreenEditBox<Integer> second;
    private final List<BalmConfigScreenEditBox<Integer>> children;

    public MergedIntEditBoxes(BalmConfigScreenContext screenContext,
                              BalmConfigScreenRowState rowState,
                              ConfiguredProperty<Integer> firstProperty,
                              ConfiguredProperty<Integer> secondProperty) {
        super(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty());

        final var state = rowState.getOrCreate(State::new);
        first = new BalmConfigScreenEditBox<>(screenContext.font(), firstProperty, screenContext, state.firstRowState);
        second = new BalmConfigScreenEditBox<>(screenContext.font(), secondProperty, screenContext, state.secondRowState);

        children = List.of(first, second);
        layoutChildren();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        layoutChildren();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        layoutChildren();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        layoutChildren();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        layoutChildren();
    }

    private void layoutChildren() {
        final var editBoxWidth = Math.max(1, (width - GAP) / 2);
        first.setPosition(getX(), getY());
        first.setSize(editBoxWidth, height);
        second.setPosition(getX() + editBoxWidth + GAP, getY());
        second.setSize(width - editBoxWidth - GAP, height);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        first.active = active;
        second.active = active;
        first.visible = visible;
        second.visible = visible;
        first.extractRenderState(graphics, mouseX, mouseY, partialTick);
        second.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected int contentHeight() {
        return height;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    private static final class State {
        private final BalmConfigScreenRowState firstRowState = new BalmConfigScreenRowState();
        private final BalmConfigScreenRowState secondRowState = new BalmConfigScreenRowState();
    }
}
