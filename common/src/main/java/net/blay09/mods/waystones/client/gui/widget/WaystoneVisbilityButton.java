package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Locale;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneVisbilityButton extends Button {

    private final WidgetSprites ACTIVATION_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_activation_button"),
            id("edit_waystone/visibility/visibility_activation_button_highlighted"));
    private final WidgetSprites GLOBAL_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_global_button"),
            id("edit_waystone/visibility/visibility_global_button_highlighted"));
    private final WidgetSprites TEAM_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_team_button"),
            id("edit_waystone/visibility/visibility_team_button_highlighted"));
    private final WidgetSprites SHARD_ONLY_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_shard_only_button"),
            id("edit_waystone/visibility/visibility_shard_only_button_highlighted"));
    private final WidgetSprites SHARESTONE_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_sharestones_button"),
            id("edit_waystone/visibility/visibility_sharestones_button_highlighted"));

    private final List<WaystoneVisibility> options;
    private final boolean canEdit;
    private WaystoneVisibility visibility;

    public WaystoneVisbilityButton(int x, int y, WaystoneVisibility visibility, List<WaystoneVisibility> options, boolean canEdit) {
        super(x, y, 21, 21, Component.empty(), _ -> {
        }, Button.DEFAULT_NARRATION);
        this.options = options;
        this.visibility = visibility;
        this.canEdit = canEdit;
        updateTooltip();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        final var sprite = getSprites().get(this.isActive(), this.isHoveredOrFocused());
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), 21, 21);
    }

    private void updateTooltip() {
        final var tooltip = Component.translatable("gui.waystones.waystone_settings.visibility." + visibility.name().toLowerCase(Locale.ROOT))
                .withStyle(ChatFormatting.YELLOW);
        if (!canEdit) {
            tooltip.append("\n").append(Component.translatable("tooltip.waystones.edit_restricted").withStyle(ChatFormatting.RED));
        }
        setTooltip(Tooltip.create(tooltip));
    }

    public WaystoneVisibility getVisibility() {
        return visibility;
    }

    private WidgetSprites getSprites() {
        return switch (visibility) {
            case ACTIVATION -> ACTIVATION_SPRITES;
            case GLOBAL -> GLOBAL_SPRITES;
            case TEAM -> TEAM_SPRITES;
            case SHARD_ONLY -> SHARD_ONLY_SPRITES;
            case SHARESTONES -> SHARESTONE_SPRITES;
        };
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (canEdit) {
            final var index = options.indexOf(visibility);
            visibility = options.get((index + 1) % options.size());
            updateTooltip();
        }
    }
}
