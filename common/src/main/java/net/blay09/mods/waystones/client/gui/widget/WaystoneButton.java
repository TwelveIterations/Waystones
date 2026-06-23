package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaystoneButton extends AbstractWaystoneButton {

    private final WarpRequirement warpRequirement;

    public WaystoneButton(int x, int y, int width, Waystone waystone, WarpRequirement warpRequirement, OnPress pressable) {
        super(x, y, width, waystone, pressable);
        final var player = Minecraft.getInstance().player;
        this.warpRequirement = warpRequirement;
        if (player == null) {
            active = false;
        } else if (!warpRequirement.canAfford(player) && !player.getAbilities().instabuild) {
            active = false;
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        renderDimensionOverlay(guiGraphics);
        renderDistance(guiGraphics);
        renderRequirements(warpRequirement, guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderDistance(GuiGraphics guiGraphics) {
        final var font = Minecraft.getInstance().font;
        final var player = Minecraft.getInstance().player;

        if (player != null && waystone.getDimension() == player.level().dimension() && isActive()) {
            int distance = (int) Math.sqrt(player.distanceToSqr(waystone.getPos().getCenter()));
            String distanceStr;
            if (distance < 10000 && (font.width(getMessage()) < 120 || distance < 1000)) {
                distanceStr = distance + "m";
            } else {
                distanceStr = String.format("%.1f", distance / 1000f).replace(",0", "").replace(".0", "") + "km";
            }
            int xOffset = getWidth() - font.width(distanceStr);
            guiGraphics.drawString(font, distanceStr, getX() + xOffset - 4, getY() + 6, 0xFFFFFF);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends WarpRequirement> void renderRequirements(T requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        final var font = Minecraft.getInstance().font;
        final var player = Minecraft.getInstance().player;
        final var renderer = RequirementClientRegistry.getRenderer((Class<T>) requirement.getClass());
        if (player != null && renderer != null) {
            renderer.renderWidget(player, requirement, guiGraphics, mouseX, mouseY, partialTicks, getX() + 2, getY() + 2);

            if (isHovered && mouseX < getX() + 2 + renderer.getWidth(player, requirement)) {
                final List<Component> tooltip = new ArrayList<>();
                warpRequirement.appendHoverText(player, tooltip);
                guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY + font.lineHeight);
            }
        }
    }

}
