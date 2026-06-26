package net.blay09.mods.waystones.client.gui.widget;

public interface ListDragController {
    void startDragging(Object entry, double mouseY);

    void dragTo(double mouseY);

    void stopDragging();

    void moveToTop(Object entry);

    void moveToBottom(Object entry);

    boolean isDragging(Object entry);
}
