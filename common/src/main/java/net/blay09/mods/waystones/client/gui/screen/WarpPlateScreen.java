package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.menu.WarpPlateContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class WarpPlateScreen extends AbstractContainerScreen<WarpPlateContainer> {

    private static final ResourceLocation WARP_PLATE_GUI_TEXTURES = new ResourceLocation(Waystones.MOD_ID, "textures/gui/menu/warp_plate.png");

    public WarpPlateScreen(WarpPlateContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 196;
        inventoryLabelY = 93;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(WARP_PLATE_GUI_TEXTURES, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        guiGraphics.blit(WARP_PLATE_GUI_TEXTURES, leftPos + 86, topPos + 34, 176, 4, 4, (int) (10 * menu.getAttunementProgress()));
        guiGraphics.blit(WARP_PLATE_GUI_TEXTURES, leftPos + 107 - (int) (10 * menu.getAttunementProgress()), topPos + 51, 176, 0, (int) (10 * menu.getAttunementProgress()), 4);
        guiGraphics.blit(WARP_PLATE_GUI_TEXTURES, leftPos + 86, topPos + 72 - (int) (10 * menu.getAttunementProgress()), 176, 4, 4, (int) (10 * menu.getAttunementProgress()));
        guiGraphics.blit(WARP_PLATE_GUI_TEXTURES, leftPos + 69, topPos + 51, 176, 0, (int) (10 * menu.getAttunementProgress()), 4);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        super.renderLabels(guiGraphics, x, y);

        Component galacticName = WarpPlateBlock.getGalacticName(menu.getWaystone());
        int width = font.width(galacticName);
        guiGraphics.drawString(font, galacticName, imageWidth - width - 5, 5, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
