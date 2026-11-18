package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.menu.WaystoneModifierMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class WaystoneModifierScreen extends AbstractContainerScreen<WaystoneModifierMenu> {

    private static final Identifier WARP_PLATE_GUI_TEXTURES = Identifier.fromNamespaceAndPath(Waystones.MOD_ID, "textures/gui/menu/waystone_modifiers.png");

    public WaystoneModifierScreen(WaystoneModifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 196);
        inventoryLabelY = 93;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics) {
        super.extractBackground(graphics);
        graphics.blit(GuiPipeline.TEXTURED, WARP_PLATE_GUI_TEXTURES, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics) {
        super.extractLabels(guiGraphics);

        Component galacticName = WarpPlateBlock.getGalacticName(menu.getWaystone().getWaystoneUid());
        int width = font.width(galacticName);
        guiGraphics.text(font, galacticName, imageWidth - width - 5, 5, 0xFFFFFFFF);
    }

}
