package net.blay09.mods.waystones.client;

import com.google.gson.JsonParser;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.util.HashMap;
import java.util.Map;

public record RuneModelDefinition(DeferredBlockStateModel model, int color, int inactiveColor) {
    public static Map<Identifier, RuneModelDefinition> loadDefinitions(BalmBlockStateModelRegistrar models, ResourceManager resources) {
        final var definitions = new HashMap<Identifier, RuneModelDefinition>();
        resources.listResources("waystone_runes", location -> location.getPath().endsWith(".json")).forEach((location, resource) -> {
            final var definitionId = location.withPath(path -> path.substring("waystone_runes/".length(), path.length() - ".json".length()));
            try (final var reader = resource.openAsReader()) {
                final var json = GsonHelper.convertToJsonObject(JsonParser.parseReader(reader), location.toString());
                final var modelId = Identifier.parse(GsonHelper.getAsString(json, "model"));
                final var color = parseColor(GsonHelper.getAsString(json, "color"));
                final var inactiveColor = json.has("inactive_color") ? parseColor(GsonHelper.getAsString(json, "inactive_color")) : 0xFF444444;
                definitions.put(definitionId, new RuneModelDefinition(models.register(modelId), color, inactiveColor));
            } catch (Exception e) {
                Waystones.logger.error("Failed to load rune definition {}; using defaults", location, e);
            }
        });
        return Map.copyOf(definitions);
    }

    private static int parseColor(String value) {
        if (!value.matches("#[0-9a-fA-F]{6}")) {
            throw new IllegalArgumentException("Expected a color in #RRGGBB format, got " + value);
        }
        return 0xFF000000 | Integer.parseInt(value.substring(1), 16);
    }
}
