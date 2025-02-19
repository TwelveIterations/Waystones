package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Supplier;

public class WaystoneInventoryButton extends Button {

    private static final ResourceLocation INVENTORY_BUTTON_TEXTURE = new ResourceLocation(Waystones.MOD_ID, "textures/gui/inventory_button.png");

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
        this.iconItem = new ItemStack(ModItems.boundScroll);
        this.iconItemHovered = new ItemStack(ModItems.warpScroll);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        visible = visiblePredicate.get();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            setX(((AbstractContainerScreenAccessor) parentScreen).getLeftPos() + xPosition.get());
            setY(((AbstractContainerScreenAccessor) parentScreen).getTopPos() + yPosition.get());
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX <getX() + width && mouseY < getY() + height;

            Player player = Minecraft.getInstance().player;
            if (PlayerWaystoneManager.canUseInventoryButton(Objects.requireNonNull(player))) {
                ItemStack icon = isHovered ? iconItemHovered : iconItem;
                guiGraphics.renderItem(icon, getX(), getY());
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, icon, getX(), getY());
            } else {
                RenderSystem.enableBlend();
                guiGraphics.setColor(1f, 1f, 1f, 0.5f);
                guiGraphics.blit(INVENTORY_BUTTON_TEXTURE, getX(), getY(), 0, 0, 16, 16, 16, 16);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }
        }
    }
}
