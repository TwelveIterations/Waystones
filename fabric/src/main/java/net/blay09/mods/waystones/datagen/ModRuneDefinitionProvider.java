package net.blay09.mods.waystones.datagen;

import com.google.gson.JsonObject;
import net.blay09.mods.waystones.api.WaystoneStyles;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ModRuneDefinitionProvider implements DataProvider {
    private final PackOutput.PathProvider paths;

    public ModRuneDefinitionProvider(FabricDataOutput output) {
        paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "waystone_runes");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        final var futures = new ArrayList<CompletableFuture<?>>();
        WaystoneStyles.getRegisteredKeys().forEach(id -> generate(output, futures, id, WaystoneStyles.getStyle(id).getRuneColor()));
        for (final var block : ModBlocks.sharestones) {
            generate(output, futures, BuiltInRegistries.BLOCK.getKey(block), block.getColor().getTextureDiffuseColor());
        }
        for (final var block : ModBlocks.portstones) {
            generate(output, futures, BuiltInRegistries.BLOCK.getKey(block), block.getColor().getTextureDiffuseColor());
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void generate(CachedOutput output, List<CompletableFuture<?>> futures, ResourceLocation id, int color) {
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
