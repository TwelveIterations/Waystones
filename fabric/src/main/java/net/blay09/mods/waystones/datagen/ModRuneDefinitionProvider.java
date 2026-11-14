package net.blay09.mods.waystones.datagen;

import com.google.gson.JsonObject;
import net.blay09.mods.waystones.api.PortstoneTypes;
import net.blay09.mods.waystones.api.SharestoneTypes;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ModRuneDefinitionProvider implements DataProvider {
    private final PackOutput.PathProvider paths;

    public ModRuneDefinitionProvider(FabricPackOutput output) {
        paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "waystone_runes");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        final var futures = new ArrayList<CompletableFuture<?>>();
        WaystoneTypes.builtinValues().forEach(type -> generate(output, futures, type.identifier(), type.runeColor()));
        SharestoneTypes.builtinValues().forEach(type -> generate(output, futures, type.identifier(), type.textureDiffuseColor()));
        PortstoneTypes.builtinValues().forEach(type -> generate(output, futures, type.identifier().withSuffix("_portstone"), type.textureDiffuseColor()));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void generate(CachedOutput output, List<CompletableFuture<?>> futures, Identifier id, int color) {
        final var json = new JsonObject();
        json.addProperty("model", id.withPath(path -> "block/" + path + "_runes").toString());
        json.addProperty("color", String.format(Locale.ROOT, "#%06X", color & 0xFFFFFF));
        json.addProperty("inactive_color", "#444444");
        futures.add(DataProvider.saveStable(output, json, paths.json(id)));
    }

    @Override
    public String getName() {
        return "Rune definitions";
    }
}
