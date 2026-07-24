package net.blay09.mods.waystones.client.config;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreen;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreenSearch;
import net.blay09.mods.balm.platform.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.platform.config.schema.ConfiguredProperty;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class WaystonesConfigScreenFactory {

    public static Screen create(Screen parent) {
        final var schema = Balm.config().getSchema(WaystonesConfig.class);
        if (schema == null) {
            return BalmConfigScreen.forMod(parent, Waystones.MOD_ID);
        }

        final var journeyMapEnabled = booleanProperty(schema, "journeyMap", "enabled");
        final var blueMapEnabled = booleanProperty(schema, "blueMap", "enabled");

        final var warpRequirements = stringListProperty(schema, "rules", "warpRequirements");

        return BalmConfigScreen.builder(Waystones.MOD_ID)
                .section(section("gameplay"), it -> it.properties(List.of(
                        property(schema, "rules", "defaultVisibility"),
                        property(schema, "rules", "allowEveryoneToManageGlobalWaystones"),
                        property(schema, "rules", "enableModifiers"))))
                .section(section("costs"), it -> it.properties(List.of(
                                property(schema, "rules", "enableXpCosts"),
                                property(schema, "rules", "enableDurability"),
                                property(schema, "rules", "enableCooldowns"),
                                property(schema, "rules", "warpSettings")))
                        .customEntry(warpRequirements, (screen, _) -> new WarpRequirementsConfigEntry(screen, warpRequirements),
                                filter -> BalmConfigScreenSearch.propertyMatches(warpRequirements, filter)))
                .section(section("entities"), it -> it.properties(List.of(
                        property(schema, "rules", "transportPets"),
                        property(schema, "rules", "transportLeashed"),
                        property(schema, "rules", "entityDenyList"))))
                .section(category("inventoryButton"), it -> it.properties(List.of(
                                property(schema, "inventoryButton", "inventoryButton")))
                        .mergedProperties(positionLabel("inventoryButton"), positionTooltip("inventoryButton"), List.of(
                                        intProperty(schema, "inventoryButton", "inventoryButtonX"),
                                        intProperty(schema, "inventoryButton", "inventoryButtonY")),
                                (screen, _, rowState) -> new MergedIntEditBoxes(
                                        screen,
                                        rowState,
                                        intProperty(schema, "inventoryButton", "inventoryButtonX"),
                                        intProperty(schema, "inventoryButton", "inventoryButtonY")))
                        .mergedProperties(positionLabel("creativeInventoryButton"), positionTooltip("creativeInventoryButton"), List.of(
                                        intProperty(schema, "inventoryButton", "creativeInventoryButtonX"),
                                        intProperty(schema, "inventoryButton", "creativeInventoryButtonY")),
                                (screen, _, rowState) -> new MergedIntEditBoxes(
                                        screen,
                                        rowState,
                                        intProperty(schema, "inventoryButton", "creativeInventoryButtonX"),
                                        intProperty(schema, "inventoryButton", "creativeInventoryButtonY"))))
                .section(category("worldGen"), it -> it.properties(categoryProperties(schema, "worldGen")))
                .section(category("client"), it -> it.properties(categoryProperties(schema, "client")))
                .section(section("mapIntegrations"), it -> it
                        .property(journeyMapEnabled)
                        .property(property(schema, "journeyMap", "preferJourneyMapIntegrationMod"), context -> context.valueFor(journeyMapEnabled))
                        .property(property(schema, "dynmap", "enabled"))
                        .property(blueMapEnabled)
                        .property(property(schema, "blueMap", "includeWaystones"), context -> context.valueFor(blueMapEnabled))
                        .property(property(schema, "blueMap", "includeSharestones"), context -> context.valueFor(blueMapEnabled))
                        .property(property(schema, "blueMap", "includeUndiscoveredWaystones"), context -> context.valueFor(blueMapEnabled)))
                .build(parent);
    }

    private static List<ConfiguredProperty<?>> categoryProperties(BalmConfigSchema schema, String category) {
        return Objects.requireNonNull(schema.findCategory(category)).properties();
    }

    private static ConfiguredProperty<?> property(BalmConfigSchema schema, String category, String property) {
        return schema.findProperty(category, property);
    }

    @SuppressWarnings("unchecked")
    private static ConfiguredProperty<Integer> intProperty(BalmConfigSchema schema, String category, String property) {
        return (ConfiguredProperty<Integer>) schema.findProperty(category, property);
    }

    @SuppressWarnings("unchecked")
    private static ConfiguredProperty<List<String>> stringListProperty(BalmConfigSchema schema, String category, String property) {
        return (ConfiguredProperty<List<String>>) schema.findProperty(category, property);
    }

    @SuppressWarnings("unchecked")
    private static ConfiguredProperty<Boolean> booleanProperty(BalmConfigSchema schema, String category, String property) {
        return (ConfiguredProperty<Boolean>) schema.findProperty(category, property);
    }

    private static Component category(String category) {
        return Component.translatable("waystones.configuration." + category);
    }

    private static Component positionLabel(String property) {
        return Component.translatable("waystones.configuration.inventoryButton." + property + "Position");
    }

    private static Component positionTooltip(String property) {
        return Component.translatable("waystones.configuration.inventoryButton." + property + "Position.tooltip");
    }

    private static Component section(String section) {
        return Component.translatable("waystones.configuration.screen." + section);
    }
}
