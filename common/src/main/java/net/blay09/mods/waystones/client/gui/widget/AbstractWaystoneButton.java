package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneGroup;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public abstract class AbstractWaystoneButton extends Button.Plain {

    protected static final int DIMENSION_OVERLAY_WIDTH = 20;

    protected final Waystone waystone;

    protected AbstractWaystoneButton(int x, int y, int width, Waystone waystone, OnPress pressable) {
        super(x, y, width, 20, getWaystoneNameComponent(waystone), pressable, Button.DEFAULT_NARRATION);
        this.waystone = waystone;
    }

    protected static Component getWaystoneNameComponent(Waystone waystone) {
        var effectiveName = waystone.getEffectiveName().copy();
        if (effectiveName.getString().isEmpty()) {
            effectiveName = Component.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        }
        final var firstGroup = getFirstGroup(waystone);
        if (firstGroup.isPresent()) {
            effectiveName.withColor(firstGroup.get().color() & 0x00FFFFFF);
        }
        return effectiveName;
    }

    private static Optional<WaystoneGroup> getFirstGroup(Waystone waystone) {
        final var player = Minecraft.getInstance().player;
        if (player == null) {
            return Optional.empty();
        }

        final var waystoneGroups = waystone.getWaystoneGroups();
        return PlayerWaystoneManager.getPlayerWaystoneData(player.level())
                .getWaystoneGroupRegistry(player)
                .stream()
                .filter(group -> waystoneGroups.contains(group.identifier()))
                .findFirst();
    }

    protected int renderDimensionOverlay(GuiGraphicsExtractor graphics) {
        final var dimensionOverlay = getDimensionOverlaySprite(waystone.getDimension());
        if (Minecraft.getInstance().getResourceManager().getResource(getSpriteResource(dimensionOverlay)).isPresent()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    dimensionOverlay,
                    getX() + getWidth() - DIMENSION_OVERLAY_WIDTH,
                    getY(),
                    DIMENSION_OVERLAY_WIDTH,
                    getHeight());
            return DIMENSION_OVERLAY_WIDTH;
        }
        return 0;
    }

    private static Identifier getDimensionOverlaySprite(ResourceKey<Level> dimension) {
        final var dimensionId = dimension.identifier();
        return id("waystone_selection/dimension/" + dimensionId.getNamespace() + "/" + dimensionId.getPath());
    }

    private static Identifier getSpriteResource(Identifier sprite) {
        return sprite.withPath(path -> "textures/gui/sprites/" + path + ".png");
    }
}
