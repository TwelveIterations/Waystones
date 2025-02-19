package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.event.server.ServerReloadedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.api.world.BiomePredicate;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.blay09.mods.waystones.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModWorldGen {
    private static final ResourceLocation waystone = new ResourceLocation("waystones", "waystone");
    private static final ResourceLocation mossyWaystone = new ResourceLocation("waystones", "mossy_waystone");
    private static final ResourceLocation sandyWaystone = new ResourceLocation("waystones", "sandy_waystone");
    private static final ResourceLocation villageWaystoneStructure = new ResourceLocation("waystones", "village/common/waystone");
    private static final ResourceLocation desertVillageWaystoneStructure = new ResourceLocation("waystones", "village/desert/waystone");
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST,
            new ResourceLocation("minecraft", "empty"));

    public static DeferredObject<PlacementModifierType<WaystonePlacement>> waystonePlacement;

    public static void initialize(BalmWorldGen worldGen) {
        worldGen.registerFeature(waystone, () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.waystone.defaultBlockState()));
        worldGen.registerFeature(mossyWaystone, () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.mossyWaystone.defaultBlockState()));
        worldGen.registerFeature(sandyWaystone, () -> new WaystoneFeature(NoneFeatureConfiguration.CODEC, ModBlocks.sandyWaystone.defaultBlockState()));

        waystonePlacement = worldGen.registerPlacementModifier(id("waystone"), () -> () -> WaystonePlacement.CODEC);

        final var IS_DESERT = TagKey.create(Registries.BIOME, new ResourceLocation("waystones", "is_desert"));
        final var IS_SWAMP = TagKey.create(Registries.BIOME, new ResourceLocation("waystones", "is_swamp"));
        final var IS_MUSHROOM = TagKey.create(Registries.BIOME, new ResourceLocation("waystones", "is_mushroom"));
        worldGen.addFeatureToBiomes(matchesTag(IS_DESERT), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.SANDY));
        worldGen.addFeatureToBiomes(matchesTag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesTag(IS_SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesTag(IS_MUSHROOM), GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY));
        worldGen.addFeatureToBiomes(matchesNeitherTag(List.of(IS_SWAMP, IS_DESERT, BiomeTags.IS_JUNGLE, IS_MUSHROOM)),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                getWaystoneFeature(WorldGenStyle.DEFAULT));

        Balm.getEvents().onEvent(ServerStartedEvent.class, event -> setupDynamicRegistries(event.getServer().registryAccess()));
        Balm.getEvents().onEvent(ServerReloadedEvent.class, event -> setupDynamicRegistries(event.getServer().registryAccess()));
    }

    private static BiomePredicate matchesTag(TagKey<Biome> tag) {
        return (resourceLocation, biome) -> biome.is(tag);
    }

    private static BiomePredicate matchesNeitherTag(List<TagKey<Biome>> tags) {
        return (resourceLocation, biome) -> {
            for (TagKey<Biome> tag : tags) {
                if (biome.is(tag)) {
                    return false;
                }
            }

            return true;
        };
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Waystones.MOD_ID, name);
    }

    private static ResourceLocation getWaystoneFeature(WorldGenStyle biomeWorldGenStyle) {
        WorldGenStyle worldGenStyle = WaystonesConfig.getActive().worldGen.worldGenStyle;
        return switch (worldGenStyle) {
            case MOSSY -> mossyWaystone;
            case SANDY -> sandyWaystone;
            case BIOME -> switch (biomeWorldGenStyle) {
                case SANDY -> sandyWaystone;
                case MOSSY -> mossyWaystone;
                default -> waystone;
            };
            default -> waystone;
        };
    }

    public static void setupDynamicRegistries(RegistryAccess registryAccess) {
        if (WaystonesConfig.getActive().worldGen.spawnInVillages || WaystonesConfig.getActive().worldGen.forceSpawnInVillages) {
            // Add Waystone to Vanilla Villages.
            addWaystoneStructureToVillageConfig(registryAccess, "village/plains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/snowy/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/savanna/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/desert/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/taiga/houses", villageWaystoneStructure, 1);
        }
    }

    private static void addWaystoneStructureToVillageConfig(RegistryAccess registryAccess, String villagePiece, ResourceLocation waystoneStructure, int weight) {

        Holder<StructureProcessorList> emptyProcessorList = registryAccess.registryOrThrow(Registries.PROCESSOR_LIST)
                .getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        LegacySinglePoolElement piece = StructurePoolElement.legacy(waystoneStructure.toString(), emptyProcessorList)
                .apply(StructureTemplatePool.Projection.RIGID);
        if (piece instanceof WaystoneStructurePoolElement element) {
            element.waystones$setIsWaystone(true);
        }
        StructureTemplatePool pool = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL).getOptional(new ResourceLocation(villagePiece)).orElse(null);
        if (pool != null) {
            var poolAccessor = (StructureTemplatePoolAccessor) pool;
            // pretty sure this can be an immutable list (when datapacked) so gotta make a copy to be safe.
            final var listOfPieces = new ObjectArrayList<>(poolAccessor.getTemplates());
            for (int i = 0; i < weight; i++) {
                listOfPieces.add(piece);
            }
            poolAccessor.setTemplates(listOfPieces);

            List<Pair<StructurePoolElement, Integer>> listOfWeightedPieces = new ArrayList<>(poolAccessor.getRawTemplates());
            listOfWeightedPieces.add(new Pair<>(piece, weight));
            poolAccessor.setRawTemplates(listOfWeightedPieces);
        }
    }
}
