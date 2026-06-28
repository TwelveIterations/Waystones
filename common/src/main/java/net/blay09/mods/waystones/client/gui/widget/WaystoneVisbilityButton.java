package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

import static net.blay09.mods.waystones.Waystones.id;

public class WaystoneVisbilityButton extends Button {

    private final WidgetSprites ACTIVATION_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_button_activation"),
            id("edit_waystone/visibility/visibility_button_activation_highlighted"));
    private final WidgetSprites GLOBAL_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_button_global"),
            id("edit_waystone/visibility/visibility_button_global_highlighted"));
    private final WidgetSprites SHARD_ONLY_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_button_shard_only"),
            id("edit_waystone/visibility/visibility_button_shard_only_highlighted"));
    private final WidgetSprites SHARESTONE_SPRITES = new WidgetSprites(
            id("edit_waystone/visibility/visibility_button_sharestone"),
            id("edit_waystone/visibility/visibility_button_sharestone_highlighted"));

    private final List<WaystoneVisibility> options;
    private final boolean canEdit;
    private WaystoneVisibility visibility;

    public WaystoneVisbilityButton(int x, int y, WaystoneVisibility visibility, List<WaystoneVisibility> options, boolean canEdit) {
        super(x, y, 18, 18, Component.empty(), button -> {
        }, Button.DEFAULT_NARRATION);
        this.options = options;
        this.visibility = visibility;
        this.canEdit = canEdit;
        updateTooltip();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        final var sprite = getSprites().get(this.isActive(), this.isHoveredOrFocused());
        guiGraphics.blitSprite(sprite, getX(), getY(), 20, 20);
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
            case SHARD_ONLY -> SHARD_ONLY_SPRITES;
            case ORANGE_SHARESTONE, GRAY_SHARESTONE, LIGHT_GRAY_SHARESTONE, BLACK_SHARESTONE, RED_SHARESTONE, GREEN_SHARESTONE, BROWN_SHARESTONE,
                 BLUE_SHARESTONE, PURPLE_SHARESTONE, CYAN_SHARESTONE, PINK_SHARESTONE, LIME_SHARESTONE, YELLOW_SHARESTONE, LIGHT_BLUE_SHARESTONE,
                 MAGENTA_SHARESTONE -> SHARESTONE_SPRITES;
        };
    }

    @Override
    public void onPress() {
        if (canEdit) {
            final var index = options.indexOf(visibility);
            visibility = options.get((index + 1) % options.size());
            updateTooltip();
        }
    }
}
