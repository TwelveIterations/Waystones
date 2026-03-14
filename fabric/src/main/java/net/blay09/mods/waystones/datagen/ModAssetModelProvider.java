package net.blay09.mods.waystones.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneType;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAssetModelProvider implements DataProvider {

    private final PackOutput.PathProvider modelPathProvider;

    public ModAssetModelProvider(FabricPackOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        copyModel(cachedOutput, futures, id("block/waystone_top"));
        copyModel(cachedOutput, futures, id("block/waystone_bottom"));
        copyModel(cachedOutput, futures, id("block/sharestone_top"));
        copyModel(cachedOutput, futures, id("block/sharestone_bottom"));
        copyModel(cachedOutput, futures, id("block/portstone_top"));
        copyModel(cachedOutput, futures, id("block/portstone_bottom"));
        copyModel(cachedOutput, futures, id("block/portstone_runes"));
        copyModel(cachedOutput, futures, id("block/warp_plate"));
        copyModel(cachedOutput, futures, id("block/warp_plate_empty"));
        copyModel(cachedOutput, futures, id("block/warp_plate_locked"));

        copyModel(cachedOutput, futures, id("item/waystone"));
        copyModel(cachedOutput, futures, id("item/sharestone"));
        copyModel(cachedOutput, futures, id("item/portstone"));

        for (final var type : WaystoneType.values()) {
            final var textures = getWaystoneTextures(type);
            final String modelName = type.getIdentifier().getPath();
            if (type == WaystoneType.ANDESITE) {
                continue;
            }

            futures.add(saveModel(cachedOutput, id("block/" + modelName + "_top"), createParentOverride("waystones:block/waystone_top", textures.blockParticle(), textures.texture())));
            futures.add(saveModel(cachedOutput, id("block/" + modelName + "_bottom"), createParentOverride("waystones:block/waystone_bottom", textures.blockParticle(), textures.texture())));
            futures.add(saveModel(cachedOutput, id("item/" + modelName), createParentOverride("waystones:item/waystone", textures.itemParticle(), textures.texture())));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Waystones Asset Models";
    }

    private void copyModel(CachedOutput cachedOutput, List<CompletableFuture<?>> futures, Identifier modelId) {
        futures.add(saveModel(cachedOutput, modelId, loadModel(modelId)));
    }

    private CompletableFuture<?> saveModel(CachedOutput cachedOutput, Identifier modelId, JsonElement json) {
        final Path path = modelPathProvider.json(modelId);
        return DataProvider.saveStable(cachedOutput, json, path);
    }

    private JsonElement loadModel(Identifier modelId) {
        final String resourcePath = "/assets/%s/models/%s.json".formatted(modelId.getNamespace(), modelId.getPath());
        try (InputStream inputStream = ModAssetModelProvider.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing model template resource: " + resourcePath);
            }

            try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read model template resource: " + resourcePath, e);
        }
    }

    private JsonObject createParentOverride(String parent, String particle, String texture) {
        final JsonObject textures = new JsonObject();
        textures.addProperty("particle", particle);
        textures.addProperty("texture", texture);

        final JsonObject root = new JsonObject();
        root.addProperty("parent", parent);
        root.add("textures", textures);
        return root;
    }

    private WaystoneTextures getWaystoneTextures(WaystoneType type) {
        return switch (type) {
            case ANDESITE -> new WaystoneTextures("block/polished_andesite", "block/polished_andesite", "waystones:block/andesite_waystone");
            case MOSSY -> new WaystoneTextures("waystones:block/mossy_andesite_waystone", "waystones:block/mossy_andesite_waystone", "waystones:block/mossy_andesite_waystone");
            case SANDY -> new WaystoneTextures("block/chiseled_sandstone", "block/chiseled_sandstone", "waystones:block/chiseled_sandstone_waystone");
            case DEEPSLATE -> new WaystoneTextures("minecraft:block/deepslate", "block/deepslate", "waystones:block/deepslate_waystone");
            case BLACKSTONE -> new WaystoneTextures("minecraft:block/blackstone", "block/blackstone", "waystones:block/blackstone_waystone");
            case END_STONE -> new WaystoneTextures("minecraft:block/end_stone_bricks", "block/end_stone", "waystones:block/end_stone_waystone");
            case RED_NETHER_BRICKS -> new WaystoneTextures("minecraft:block/red_nether_bricks", "block/red_nether_bricks", "waystones:block/red_nether_bricks_waystone");
            case PURPUR -> new WaystoneTextures("minecraft:block/purpur_block", "block/purpur_block", "waystones:block/purpur_waystone");
            case PRISMARINE -> new WaystoneTextures("minecraft:block/prismarine", "block/prismarine", "waystones:block/prismarine_waystone");
            case MUD_BRICKS -> new WaystoneTextures("minecraft:block/mud_bricks", "block/mud_bricks", "waystones:block/mud_bricks_waystone");
        };
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Waystones.MOD_ID, path);
    }

    private record WaystoneTextures(String blockParticle, String itemParticle, String texture) {
    }
}
