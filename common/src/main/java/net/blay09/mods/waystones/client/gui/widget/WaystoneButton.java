package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.requirement.WarpRequirement;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaystoneButton extends Button {


    private final WarpRequirement warpRequirement;
    private final Waystone waystone;

    public WaystoneButton(int x, int y, Waystone waystone, WarpRequirement warpRequirement, OnPress pressable) {
        super(x, y, 200, 20, getWaystoneNameComponent(waystone), pressable, Button.DEFAULT_NARRATION);
        Player player = Minecraft.getInstance().player;
        this.warpRequirement = warpRequirement;
        this.waystone = waystone;
        if (player == null) {
            active = false;
        } else if (!warpRequirement.canAfford(player) && !player.getAbilities().instabuild) {
            active = false;
        }
    }

    private static Component getWaystoneNameComponent(Waystone waystone) {
        var effectiveName = waystone.getName().copy();
        if (effectiveName.getString().isEmpty()) {
            effectiveName = Component.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        }
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)) {
            effectiveName.withStyle(ChatFormatting.YELLOW);
        }
        return effectiveName;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

        final var font = Minecraft.getInstance().font;
        final var player = Minecraft.getInstance().player;

        // render distance
        if (waystone.getDimension() == player.level().dimension() && isActive()) {
            int distance = (int) player.position().distanceTo(waystone.getPos().getCenter());
            String distanceStr;
            if (distance < 10000 && (font.width(getMessage()) < 120 || distance < 1000)) {
                distanceStr = distance + "m";
            } else {
                // sorry for ugly code, chatgpt was down and this was the only thing my dumbed down brain could come up with
                distanceStr = String.format("%.1f", distance / 1000f).replace(",0", "").replace(".0", "") + "km";
            }
            int xOffset = getWidth() - font.width(distanceStr);
            guiGraphics.drawString(font, distanceStr, getX() + xOffset - 4, getY() + 6, 0xFFFFFF);
        }

        renderRequirements(warpRequirement, guiGraphics, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("unchecked")
    private <T extends WarpRequirement> void renderRequirements(T requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        final var font = Minecraft.getInstance().font;
        final var player = Minecraft.getInstance().player;
        final var renderer = RequirementClientRegistry.getRenderer((Class<T>) requirement.getClass());
        if (renderer != null) {
            renderer.renderWidget(player, requirement, guiGraphics, mouseX, mouseY, partialTicks, getX() + 2, getY() + 2);

            if (isHovered && mouseX < getX() + 2 + renderer.getWidth(player, requirement)) {
                final List<Component> tooltip = new ArrayList<>();
                warpRequirement.appendHoverText(player, tooltip);
                guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), mouseX, mouseY + font.lineHeight);
            }
        }
    }

}
