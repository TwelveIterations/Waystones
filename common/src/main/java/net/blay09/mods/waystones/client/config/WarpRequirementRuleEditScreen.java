package net.blay09.mods.waystones.client.config;

import net.blay09.mods.shogi.client.gui.RuleEditBox;
import net.blay09.mods.waystones.config.WaystonesRules;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class WarpRequirementRuleEditScreen extends Screen {
    private static final String HELP_URL = "https://mods.twelveiterations.com/minecraft/waystones/guides/warp-rules";
    private static final Component TITLE = Component.translatable("waystones.configuration.rules.warpRequirements.edit");
    private static final Component PLACEHOLDER = Component.translatable("waystones.configuration.rules.warpRequirements.placeholder");
    private static final Component HELP_LABEL = Component.translatable("waystones.configuration.rules.warpRequirements.help");

    private final @Nullable Screen parent;
    private final String initialValue;
    private final Consumer<String> valueConsumer;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 28, 33);

    private @Nullable RuleEditBox ruleEditBox;

    public WarpRequirementRuleEditScreen(@Nullable Screen parent, String initialValue, Consumer<String> valueConsumer) {
        super(TITLE);
        this.parent = parent;
        this.initialValue = initialValue;
        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void init() {
        final var header = layout.addToHeader(LinearLayout.vertical());
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(title, font));

        ruleEditBox = RuleEditBox.builder()
                .setScope(WaystonesRules.scope)
                .setPlaceholder(PLACEHOLDER)
                .build(font, Math.max(1, width - 40), Math.max(1, height - layout.getHeaderHeight() - layout.getFooterHeight() - 20), title);
        ruleEditBox.setCharacterLimit(2048);
        ruleEditBox.setValue(initialValue);

        layout.addToContents(new FrameLayout()).addChild(ruleEditBox);

        final var footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(HELP_LABEL, ConfirmLinkScreen.confirmLink(this, HELP_URL)).build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> saveAndClose()).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, _ -> onClose()).build());
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
        setInitialFocus(ruleEditBox);
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (ruleEditBox != null) {
            ruleEditBox.setX(20);
            ruleEditBox.setY(layout.getHeaderHeight() + 10);
            ruleEditBox.setWidth(Math.max(1, width - 40));
            ruleEditBox.setHeight(Math.max(1, height - layout.getHeaderHeight() - layout.getFooterHeight() - 20));
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            onClose();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }

    private void saveAndClose() {
        if (ruleEditBox != null) {
            valueConsumer.accept(ruleEditBox.getValue());
        }
        onClose();
    }
}
