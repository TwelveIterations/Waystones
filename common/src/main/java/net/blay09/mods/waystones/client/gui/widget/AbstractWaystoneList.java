package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

public abstract class AbstractWaystoneList<E extends AbstractWaystoneList.Entry<E>> extends ContainerObjectSelectionList<E> {

    public static final int ENTRY_WIDTH = 220;
    public static final int ENTRY_HEIGHT = 22;

    protected AbstractWaystoneList(int x, int y, int width, int height) {
        super(Minecraft.getInstance(), width, height, y, ENTRY_HEIGHT);
        setX(x);
    }

    public void setWaystones(List<Waystone> waystones) {
        clearEntries();
        for (int i = 0; i < waystones.size(); i++) {
            addEntry(createEntry(waystones.get(i), i, waystones.size()));
        }
        setScrollAmount(Math.min(getScrollAmount(), Math.max(0, getMaxPosition() - height)));
    }

    protected abstract E createEntry(Waystone waystone, int index, int waystoneCount);

    @Override
    public int getRowWidth() {
        return ENTRY_WIDTH;
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics graphics) {
    }

    public abstract static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {

        protected int x;
        protected int y;

        protected abstract List<AbstractWidget> widgets();

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets();
        }

        @Override
        public void render(GuiGraphics graphics,
                           int index,
                           int top,
                           int left,
                           int width,
                           int height,
                           int mouseX,
                           int mouseY,
                           boolean hovered,
                           float partialTick) {
            x = left;
            y = top;
            updateWidgetPositions();
            for (AbstractWidget widget : widgets()) {
                widget.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        protected void updateWidgetPositions() {
        }
    }
}
