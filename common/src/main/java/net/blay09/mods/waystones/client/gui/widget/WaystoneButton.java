package net.blay09.mods.waystones.client.gui.widget;

import com.mojang.datafixers.util.Either;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneKinds;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.client.requirement.RequirementClientRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WaystoneButton extends Button.Plain {

    private final Either<List<Object>, List<Object>> warpRequirements;
    private final Waystone waystone;

    public WaystoneButton(int x,
                          int y,
                          int width,
                          Waystone waystone,
                          Either<List<Object>, List<Object>> warpRequirements,
                          OnPress pressable) {
        super(x, y, width, 20, getWaystoneNameComponent(waystone), pressable, Button.DEFAULT_NARRATION);
        this.warpRequirements = warpRequirements;
        this.waystone = waystone;
        active = warpRequirements.left().isPresent();
    }

    private static Component getWaystoneNameComponent(Waystone waystone) {
        var effectiveName = waystone.getName().copy();
        if (effectiveName.getString().isEmpty()) {
            effectiveName = Component.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        }
        if (waystone.getVisibility() == WaystoneVisibility.GLOBAL && waystone.getWaystoneKind().equals(WaystoneKinds.WAYSTONE)) {
            effectiveName.withStyle(ChatFormatting.YELLOW);
        }
        return effectiveName;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractDefaultSprite(graphics);

        final int leftPadding = renderRequirements(graphics, mouseX, mouseY, partialTicks);
        final int rightPadding = renderDistance(graphics);
        final int labelLeft = getX() + leftPadding + TEXT_MARGIN;
        final int labelRight = getX() + getWidth() - TEXT_MARGIN - rightPadding;
        final int labelTop = getY();
        final int labelBottom = getY() + getHeight();
        final var buttonTextOutput = graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        buttonTextOutput.acceptScrolling(message, getX() + getWidth() / 2, labelLeft, labelRight, labelTop, labelBottom);
    }

    private int renderDistance(GuiGraphicsExtractor graphics) {
        final var font = Minecraft.getInstance().font;
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (waystone.getDimension() == player.level().dimension() && isActive()) {
            int distance = (int) player.position().distanceTo(waystone.getPos().getCenter());
            String distanceStr;
            if (distance < 10000 && (font.width(getMessage()) < 120 || distance < 1000)) {
                distanceStr = distance + "m";
            } else {
                // sorry for ugly code, chatgpt was down and this was the only thing my dumbed down brain could come up with
                distanceStr = String.format("%.1f", distance / 1000f).replace(",0", "").replace(".0", "") + "km";
            }
            int distanceWidth = font.width(distanceStr);
            graphics.text(font, distanceStr, getX() + getWidth() - distanceWidth - 4, getY() + 6, 0xFFFFFFFF);
            return distanceWidth + 5;
        }
        return 0;
    }

    private int renderRequirements(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        final var font = Minecraft.getInstance().font;
        final var player = Objects.requireNonNull(Minecraft.getInstance().player);
        final var requirement = Either.unwrap(warpRequirements);
        final var renderer = RequirementClientRegistry.getListRenderer();
        renderer.renderWidget(player, requirement, guiGraphics, mouseX, mouseY, partialTicks, getX() + 2, getY() + 2);

        if (isHovered && mouseX < getX() + 2 + renderer.getWidth(player, requirement)) {
            final List<Component> tooltip = new ArrayList<>();
            renderer.appendHoverText(player, requirement, tooltip);
            guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), mouseX, mouseY + font.lineHeight);
        }

        return renderer.getWidth(player, requirement) + 5;
    }

}
