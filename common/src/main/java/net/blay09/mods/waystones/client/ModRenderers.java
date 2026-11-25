package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.waystones.api.PortstoneType;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.PortstoneBlock;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.FleetingMemorialRenderer;
import net.blay09.mods.waystones.client.render.PortstoneRenderer;
import net.blay09.mods.waystones.client.render.SharestoneRenderer;
import net.blay09.mods.waystones.client.render.WaystoneRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.blay09.mods.waystones.Waystones.id;

public class ModRenderers {
    private static Map<Identifier, RuneModelDefinition> runeDefinitions = Map.of();
    private static final Map<Identifier, DeferredBlockStateModel> runeModels = new HashMap<>();

    private static final Map<WaystoneType, RuneModelDefinition> waystoneRunes = new HashMap<>();
    private static final Map<SharestoneType, RuneModelDefinition> sharestoneRunes = new HashMap<>();
    private static final Map<PortstoneType, RuneModelDefinition> portstoneRunes = new HashMap<>();

    public static void initialize(BalmBlockStateModelRegistrar models) {
        runeModels.clear();
        for (final var family : List.of("waystone", "portstone", "sharestone")) {
            final var modelId = id("block/" + family + "_runes");
            runeModels.put(modelId, models.register(modelId));
        }

        final var resources = Minecraft.getInstance().getResourceManager();
        //noinspection ConstantValue // temporarily needed because Balm's noop runs this too early
        if (resources != null) {
            RuneModelDefinition.loadDefinitions(resources).values().forEach(definition ->
                    runeModels.computeIfAbsent(definition.model(), models::register));
        }
    }

    public static BlockStateModel getRuneModel(Identifier modelId) {
        final var model = runeModels.get(modelId);
        return model != null ? model.asBlockStateModel() : Minecraft.getInstance().getModelManager().getBlockStateModelSet().missingModel();
    }

    public static class RuneDefinitionsReloadListener extends SimplePreparableReloadListener<Map<Identifier, RuneModelDefinition>> {
        @Override
        protected Map<Identifier, RuneModelDefinition> prepare(ResourceManager resources, ProfilerFiller profiler) {
            return RuneModelDefinition.loadDefinitions(resources);
        }

        @Override
        protected void apply(Map<Identifier, RuneModelDefinition> definitions, ResourceManager resources, ProfilerFiller profiler) {
            runeDefinitions = definitions;
            waystoneRunes.clear();
            sharestoneRunes.clear();
            portstoneRunes.clear();
        }
    }

    public static RuneModelDefinition getWaystoneRunes(WaystoneType type) {
        return waystoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier(), new RuneModelDefinition(id("block/waystone_runes"), key.runeColor(), 0xFF444444)));
    }

    public static RuneModelDefinition getSharestoneRunes(SharestoneType type) {
        return sharestoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier(), new RuneModelDefinition(id("block/sharestone_runes"), key.textureDiffuseColor(), 0xFF444444)));
    }

    public static RuneModelDefinition getPortstoneRunes(PortstoneType type) {
        return portstoneRunes.computeIfAbsent(type, key -> resolveRunes(key.identifier().withSuffix("_portstone"), new RuneModelDefinition(id("block/portstone_runes"), key.textureDiffuseColor(), 0xFF444444)));
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
