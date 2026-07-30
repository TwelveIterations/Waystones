package net.blay09.mods.waystones.client.config;

import net.blay09.mods.balm.client.platform.config.screen.list.BalmConfigListEditorContext;
import net.blay09.mods.balm.client.platform.config.screen.list.BalmConfigListEditorEntry;
import net.blay09.mods.balm.client.platform.config.screen.list.internal.BalmConfigListEditorValue;
import net.blay09.mods.shogi.common.util.ShogiExpressionHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class WarpRequirementListEditorEntry extends BalmConfigListEditorEntry<String> {
    private static final Component EDIT_LABEL = Component.translatable("gui.balm.configuration.edit");
    private static final Component DELETE_LABEL = Component.translatable("gui.balm.configuration.delete");
    private static final Component RESET_LABEL = Component.translatable("gui.balm.configuration.reset");

    private final Button editButton;
    private final Button deleteButton;
    private final Button resetButton;

    public WarpRequirementListEditorEntry(BalmConfigListEditorContext<String> context, BalmConfigListEditorValue<String> valueHolder) {
        super(context, valueHolder);
        editButton = Button.builder(EDIT_LABEL, this::onEditButton)
                .size(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT)
                .build();
        deleteButton = Button.builder(DELETE_LABEL, this::onDeleteButton)
                .size(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT)
                .build();
        resetButton = Button.builder(RESET_LABEL, this::onResetButton)
                .size(ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT)
                .build();
        addActionWidget(editButton);
        addActionWidget(deleteButton);
        addActionWidget(resetButton);
    }

    @Override
    public void startEditing(@Nullable Object initialValue) {
        final var value = initialValue != null ? String.valueOf(initialValue) : Objects.requireNonNullElse(valueHolder.value(), "");
        openRuleEditor(value);
    }

    @Override
    protected void extractEntryContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
        final var value = Objects.requireNonNullElse(valueHolder.value(), "");
        final var textWidth = Math.max(1, getContentRightBeforeActions() - getContentLeftAfterDragHandle());
        final var label = context.font().plainSubstrByWidth(value, textWidth);
        graphics.text(context.font(),
                ShogiExpressionHighlighter.highlightComponent(label),
                getContentLeftAfterDragHandle(),
                getContentY() + 5,
                0xFFFFFFFF);
    }

    @Override
    protected void updateActionWidgets() {
        editButton.active = canEdit();
        deleteButton.active = canDelete();
        resetButton.visible = canReset();
        resetButton.active = canReset();
    }

    private boolean canEdit() {
        return true;
    }

    private void onEditButton(Button button) {
        if (canEdit()) {
            startEditing(null);
        }
    }

    private boolean canDelete() {
        return true;
    }

    private void onDeleteButton(Button button) {
        if (canDelete()) {
            context.delete(this);
        }
    }

    private boolean canReset() {
        return valueHolder.originalValue() != null && !Objects.equals(valueHolder.value(), valueHolder.originalValue());
    }

    private void onResetButton(Button button) {
        if (canReset()) {
            valueHolder.reset();
            context.revalidate();
        }
    }

    private void openRuleEditor(String value) {
        final Screen parent = context instanceof Screen screen ? screen : null;
        Minecraft.getInstance().gui.setScreen(new WarpRequirementRuleEditScreen(parent,
                value,
                newValue -> {
                    valueHolder.value(newValue);
                    context.revalidate();
                    context.commit();
                }));
    }
}
