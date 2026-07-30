package net.blay09.mods.waystones.client.config;

import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreen;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreenLabeledEntry;
import net.blay09.mods.balm.client.platform.config.screen.list.BalmConfigListEditorScreen;
import net.blay09.mods.balm.platform.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.platform.config.util.ConfigLocalization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class WarpRequirementsConfigEntry extends BalmConfigScreenLabeledEntry {

    private final ConfiguredProperty<List<String>> property;

    public WarpRequirementsConfigEntry(BalmConfigScreen screen, ConfiguredProperty<List<String>> property) {
        super(screen,
                Component.translatable(ConfigLocalization.forProperty(property)),
                Component.translatable(ConfigLocalization.forPropertyTooltip(property)),
                Button.builder(
                                Component.translatable("gui.balm.configuration.list.items", screen.bindingFor(property).get().size()),
                                _ -> Minecraft.getInstance().gui.setScreen(createListEditor(screen, property)))
                        .width(Button.DEFAULT_WIDTH)
                        .build());
        this.property = property;
    }

    @Override
    protected @Nullable Component getValidationError() {
        return context.getValidationError(property);
    }

    private static BalmConfigListEditorScreen<String> createListEditor(BalmConfigScreen screen, ConfiguredProperty<List<String>> property) {
        final var binding = screen.bindingFor(property);
        return BalmConfigListEditorScreen.builder(screen, screen, binding)
                .customizeEntries(WarpRequirementListEditorEntry::new)
                .searchable((value, filter) -> value.toLowerCase(Locale.ROOT).contains(filter))
                .build();
    }
}
