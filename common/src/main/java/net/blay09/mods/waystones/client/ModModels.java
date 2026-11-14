package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.waystones.api.WaystoneStyles;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public class ModModels {
    private static Map<ResourceLocation, RuneModelDefinition> runeDefinitions = Map.of();
    private static final Map<Block, RuneModelDefinition> runesByBlock = new HashMap<>();

    public static void initialize(BalmBlockStateModelRegistrar models) {
        final var definitions = new HashMap<>(RuneModelDefinition.loadDefinitions(models, Minecraft.getInstance().getResourceManager()));
        for (final var family : List.of("waystone", "portstone", "sharestone")) {
            final var modelId = id("block/" + family + "_runes");
            definitions.put(modelId, new RuneModelDefinition(models.register(modelId), 0xFFFFFFFF, 0xFF444444));
        }
        runeDefinitions = Map.copyOf(definitions);
        runesByBlock.clear();
    }

    public static RuneModelDefinition getWaystoneRunes(Block block) {
        return runesByBlock.computeIfAbsent(block, key -> {
            final var style = WaystoneStyles.getStyle(key);
            return resolveRunes(key, "waystone", style != null ? style.getRuneColor() : 0xFFFFFFFF);
        });
    }

    public static RuneModelDefinition getSharestoneRunes(SharestoneBlock block) {
        return runesByBlock.computeIfAbsent(block, key -> resolveRunes(key, "sharestone", block.getColor().getTextureDiffuseColor()));
    }

    public static RuneModelDefinition getPortstoneRunes(PortstoneBlock block) {
        return runesByBlock.computeIfAbsent(block, key -> resolveRunes(key, "portstone", block.getColor().getTextureDiffuseColor()));
    }

    private static RuneModelDefinition resolveRunes(Block block, String family, int color) {
        return runeDefinitions.getOrDefault(BuiltInRegistries.BLOCK.getKey(block),
                new RuneModelDefinition(runeDefinitions.get(id("block/" + family + "_runes")).model(), color, 0xFF444444));
    }
}
