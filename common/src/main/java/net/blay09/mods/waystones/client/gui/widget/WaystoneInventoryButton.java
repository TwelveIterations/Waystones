package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.waystones.client.InventoryButtonGuiHandler;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneInventoryButton extends Button {

    private static final Identifier INVENTORY_BUTTON_SPRITE = id("inventory/inventory_button");

    private final AbstractContainerScreen<?> parentScreen;
    private final ItemStack iconItem;
    private final ItemStack iconItemHovered;
    private final Supplier<Boolean> visiblePredicate;
    private final Supplier<Integer> xPosition;
    private final Supplier<Integer> yPosition;

    public WaystoneInventoryButton(AbstractContainerScreen<?> parentScreen, OnPress pressable, Supplier<Boolean> visiblePredicate, Supplier<Integer> xPosition, Supplier<Integer> yPosition) {
        super(0, 0, 16, 16, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.parentScreen = parentScreen;
        this.visiblePredicate = visiblePredicate;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.iconItem = ModItems.boundScroll.createStack();
        this.iconItemHovered = ModItems.warpScroll.createStack();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics) {
        visible = visiblePredicate.get();
        if (visible) {
            setX(((AbstractContainerScreenAccessor) parentScreen).getLeftPos() + xPosition.get());
            setY(((AbstractContainerScreenAccessor) parentScreen).getTopPos() + yPosition.get());
            isHovered = graphics.mouseX() >= getX() && graphics.mouseY() >= getY() && graphics.mouseX() < getX() + width && graphics.mouseY() < getY() + height;

            if (InventoryButtonGuiHandler.getRequirements().left().isPresent()) {
                ItemStack icon = isHovered ? iconItemHovered : iconItem;
                graphics.item(icon, getX(), getY());
                graphics.itemDecorations(Minecraft.getInstance().font, icon, getX(), getY());
            } else {
                graphics.blitSprite(GuiPipeline.TEXTURED, INVENTORY_BUTTON_SPRITE, getX(), getY(), 16, 16, 0x80FFFFFF);
            }
        }
    }
}
