package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractWaystoneList<E extends AbstractWaystoneList.Entry<E>> extends ContainerObjectSelectionList<E> {

    public static final int ENTRY_WIDTH = 220;
    public static final int ENTRY_HEIGHT = 22;

    protected AbstractWaystoneList(int x, int y, int width, int height) {
        super(Minecraft.getInstance(), width, height, y, ENTRY_HEIGHT);
        setX(x);
    }

    public void setWaystones(List<? extends Waystone> waystones) {
        clearEntries();
        for (int i = 0; i < waystones.size(); i++) {
            addEntry(createEntry(waystones.get(i), i, waystones.size()));
        }
        refreshScrollAmount();
    }

    protected abstract E createEntry(Waystone waystone, int index, int waystoneCount);

    @Override
    public int getRowWidth() {
        return ENTRY_WIDTH;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {
    }

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor graphics) {
    }

    public abstract static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {

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
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            for (AbstractWidget widget : widgets()) {
                widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            widgets().forEach(consumer);
        }
    }
}
