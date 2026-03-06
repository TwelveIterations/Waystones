package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.waystones.api.TeleportFlags;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.blay09.mods.waystones.core.InvalidWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class WaystoneInventoryButton extends Button {

    private static final Identifier INVENTORY_BUTTON_SPRITE = Identifier.withDefaultNamespace("waystones/inventory_button");

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
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        visible = visiblePredicate.get();
        if (visible) {
            setX(((AbstractContainerScreenAccessor) parentScreen).getLeftPos() + xPosition.get());
            setY(((AbstractContainerScreenAccessor) parentScreen).getTopPos() + yPosition.get());
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;

            final var player = Minecraft.getInstance().player;
            final var waystone = PlayerWaystoneManager.getInventoryButtonTarget(player).orElse(InvalidWaystone.INSTANCE);
            final var context = WaystonesAPI.createUnboundTeleportContext(player, waystone).addFlag(TeleportFlags.INVENTORY_BUTTON);
            final var requirements = WaystonesRules.inventoryButtonWarpRequirements.get(context);
            if (requirements.left().isPresent()) {
                ItemStack icon = isHovered ? iconItemHovered : iconItem;
                guiGraphics.renderItem(icon, getX(), getY());
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, icon, getX(), getY());
            } else {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INVENTORY_BUTTON_SPRITE, getX(), getY(), 16, 16, 0x80FFFFFF);
            }
        }
    }
}
