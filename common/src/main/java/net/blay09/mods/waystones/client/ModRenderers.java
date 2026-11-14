package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.api.PortstoneType;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public class ModRenderers {
    private static Map<Identifier, RuneModelDefinition> runeDefinitions = Map.of();

    private static final Map<WaystoneType, RuneModelDefinition> waystoneRunes = new HashMap<>();
    private static final Map<SharestoneType, RuneModelDefinition> sharestoneRunes = new HashMap<>();
    private static final Map<PortstoneType, RuneModelDefinition> portstoneRunes = new HashMap<>();

    public static void initialize(BalmBlockStateModelRegistrar models) {
        final var definitions = new HashMap<>(RuneModelDefinition.loadDefinitions(models, Minecraft.getInstance().getResourceManager()));
        for (final var family : List.of("waystone", "portstone", "sharestone")) {
            final var modelId = id("block/" + family + "_runes");
            definitions.put(modelId, new RuneModelDefinition(models.register(modelId), 0xFFFFFFFF, 0xFF444444));
        }
        runeDefinitions = Map.copyOf(definitions);
        waystoneRunes.clear();
        sharestoneRunes.clear();
        portstoneRunes.clear();
    }

    public static RuneModelDefinition getWaystoneRunes(WaystoneType type) {
        return waystoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier(), new RuneModelDefinition(runeDefinitions.get(id("block/waystone_runes")).model(), key.runeColor(), 0xFF444444)));
    }

    public static RuneModelDefinition getSharestoneRunes(SharestoneType type) {
        return sharestoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier(), new RuneModelDefinition(runeDefinitions.get(id("block/sharestone_runes")).model(), key.textureDiffuseColor(), 0xFF444444)));
    }

    public static RuneModelDefinition getPortstoneRunes(PortstoneType type) {
        return portstoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier().withSuffix("_portstone"), new RuneModelDefinition(runeDefinitions.get(id("block/portstone_runes")).model(), key.textureDiffuseColor(), 0xFF444444)));
    }

    private static RuneModelDefinition resolveRunes(Identifier id, RuneModelDefinition fallback) {
        return runeDefinitions.getOrDefault(id, fallback);
    }

    public static void initialize(BalmBlockEntityRendererRegistrar renderers) {
        renderers.register(ModBlockEntities.waystone, WaystoneRenderer::new);
        renderers.register(ModBlockEntities.sharestone, SharestoneRenderer::new);
        renderers.register(ModBlockEntities.portstone, PortstoneRenderer::new);
        renderers.register(ModBlockEntities.fleetingMemorial, FleetingMemorialRenderer::new);
    }

    public static void initialize(BalmBlockColorRegistrar blockColors) {
        blockColors.register(List.of(BlockTintSources.constant(0xffc456bd)), ModBlocks.warpPlate);
        blockColors.register(List.of(state -> ((SharestoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.sharestones.values());
        blockColors.register(List.of(state -> ((PortstoneBlock) state.getBlock()).getType().textColor() | 0xFF000000), ModBlocks.portstones.values());
    }

}
