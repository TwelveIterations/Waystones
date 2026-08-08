package net.blay09.mods.waystones.worldgen;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.platform.event.callback.ServerLifecycleCallback;
import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.balm.world.level.biome.BiomePredicate;
import net.blay09.mods.balm.world.level.levelgen.BalmWorldGen;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneOrigin;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WorldGenStyle;
import net.blay09.mods.waystones.mixin.StructureTemplatePoolAccessor;
import net.blay09.mods.waystones.tag.ModBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModWorldGen {
    private static final Identifier waystone = Identifier.fromNamespaceAndPath("waystones", "waystone");
    private static final Identifier mossyWaystone = Identifier.fromNamespaceAndPath("waystones", "mossy_waystone");
    private static final Identifier sandyWaystone = Identifier.fromNamespaceAndPath("waystones", "sandy_waystone");
    private static final Identifier blackstoneWaystone = Identifier.fromNamespaceAndPath("waystones", "blackstone_waystone");
    private static final Identifier redNetherBricksWaystone = Identifier.fromNamespaceAndPath("waystones", "red_nether_bricks_waystone");
    private static final Identifier deepslateWaystone = Identifier.fromNamespaceAndPath("waystones", "deepslate_waystone");
    private static final Identifier endStoneWaystone = Identifier.fromNamespaceAndPath("waystones", "end_stone_waystone");
    private static final Identifier purpurWaystone = Identifier.fromNamespaceAndPath("waystones", "purpur_waystone");
    private static final Identifier prismarineWaystone = Identifier.fromNamespaceAndPath("waystones", "prismarine_waystone");
    private static final Identifier mudBricksWaystone = Identifier.fromNamespaceAndPath("waystones", "mud_bricks_waystone");
    private static final Identifier villageWaystoneStructure = Identifier.fromNamespaceAndPath("waystones", "village/common/waystone");
    private static final Identifier desertVillageWaystoneStructure = Identifier.fromNamespaceAndPath("waystones", "village/desert/waystone");
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST,
            Identifier.fromNamespaceAndPath("minecraft", "empty"));

    public static Holder<PlacementModifierType<?>> waystonePlacement;

    public static void initializeFeatures(BalmRegistrar.Scoped<Feature<?>> registrar) {
        registrar.register(waystone.getPath(), (id) -> new WaystoneFeature());
    }

    public static void initializePlacementModifierTypes(BalmRegistrar.Scoped<PlacementModifierType<?>> registrar) {
        waystonePlacement = registrar.register("waystone", (id) -> (PlacementModifierType<WaystonePlacement>) () -> WaystonePlacement.CODEC);
    }

    public static void initializePoiTypes(BalmPoiTypeRegistrar registrar) {
        registrar.register("wild_waystone", () -> new PoiType(gatherWaystonesOfOrigin(WaystoneOrigin.WILDERNESS), 1, 1));
        registrar.register("village_waystone", () -> new PoiType(gatherWaystonesOfOrigin(WaystoneOrigin.VILLAGE), 1, 1));
    }

    public static void initialize(BalmWorldGen worldGen) {
        Balm.config().onConfigAvailable(WaystonesConfig.class, (config) -> {
            worldGen.modifyBiome(
                    id("add_sandy_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_SANDY_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.SANDY)));
            worldGen.modifyBiome(
                    id("add_mossy_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_MOSSY_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MOSSY)));
            worldGen.modifyBiome(
                    id("add_blackstone_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_BLACKSTONE_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.BLACKSTONE)));
            worldGen.modifyBiome(
                    id("add_red_nether_bricks_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_RED_NETHER_BRICKS_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.RED_NETHER_BRICKS)));
            worldGen.modifyBiome(
                    id("add_end_stone_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_END_STONE_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.END_STONE)));
            worldGen.modifyBiome(
                    id("add_purpur_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_PURPUR_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.PURPUR)));
            worldGen.modifyBiome(
                    id("add_deepslate_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_DEEPSLATE_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.DEEPSLATE)));
            worldGen.modifyBiome(
                    id("add_prismarine_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_PRISMARINE_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.PRISMARINE)));
            worldGen.modifyBiome(
                    id("add_mud_bricks_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_MUD_BRICKS_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.MUD_BRICKS)));
            worldGen.modifyBiome(
                    id("add_waystone"),
                    matchesTag(ModBiomeTags.HAS_STRUCTURE_WAYSTONE),
                    (biome, builder) -> builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, getWaystoneFeature(WorldGenStyle.DEFAULT)));
        });

        ServerLifecycleCallback.Starting.EVENT.register(server -> setupDynamicRegistries(server.registryAccess()));
    }

    private static Set<BlockState> gatherWaystonesOfOrigin(WaystoneOrigin origin) {
        return ModBlocks.waystones.values().stream()
                .flatMap(it -> it.asBlock().getStateDefinition().getPossibleStates().stream())
                .filter(it -> it.getValue(WaystoneBlock.ORIGIN) == origin)
                .collect(Collectors.toSet());
    }

    private static BiomePredicate matchesTag(TagKey<Biome> tag) {
        return biomeHolder -> biomeHolder.is(tag);
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(Waystones.MOD_ID, name);
    }

    private static ResourceKey<PlacedFeature> getWaystoneFeature(WorldGenStyle biomeWorldGenStyle) {
        WorldGenStyle worldGenStyle = WaystonesConfig.getActive().worldGen.wildWaystoneStyle;
        final var identifier = switch (worldGenStyle) {
            case MOSSY -> mossyWaystone;
            case SANDY -> sandyWaystone;
            case BLACKSTONE -> blackstoneWaystone;
            case RED_NETHER_BRICKS -> redNetherBricksWaystone;
            case DEEPSLATE -> deepslateWaystone;
            case END_STONE -> endStoneWaystone;
            case PURPUR -> purpurWaystone;
            case PRISMARINE -> prismarineWaystone;
            case MUD_BRICKS -> mudBricksWaystone;
            case BIOME -> switch (biomeWorldGenStyle) {
                case SANDY -> sandyWaystone;
                case MOSSY -> mossyWaystone;
                case BLACKSTONE -> blackstoneWaystone;
                case RED_NETHER_BRICKS -> redNetherBricksWaystone;
                case DEEPSLATE -> deepslateWaystone;
                case END_STONE -> endStoneWaystone;
                case PURPUR -> purpurWaystone;
                case PRISMARINE -> prismarineWaystone;
                case MUD_BRICKS -> mudBricksWaystone;
                default -> waystone;
            };
            default -> waystone;
        };
        return ResourceKey.create(Registries.PLACED_FEATURE, identifier);
    }

    public static void setupDynamicRegistries(RegistryAccess registryAccess) {
        if (WaystonesConfig.getActive().worldGen.spawnInVillages != WaystonesConfig.VillageWaystoneGeneration.DISABLED) {
            // Add Waystone to Vanilla Villages.
            addWaystoneStructureToVillageConfig(registryAccess, "village/plains/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/snowy/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/savanna/houses", villageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/desert/houses", desertVillageWaystoneStructure, 1);
            addWaystoneStructureToVillageConfig(registryAccess, "village/taiga/houses", villageWaystoneStructure, 1);
        }
    }

    private static void addWaystoneStructureToVillageConfig(RegistryAccess registryAccess, String villagePiece, Identifier waystoneStructure, int weight) {
        Holder<StructureProcessorList> emptyProcessorList = registryAccess.lookupOrThrow(Registries.PROCESSOR_LIST)
                .getOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        LegacySinglePoolElement piece = StructurePoolElement.legacy(waystoneStructure.toString(), emptyProcessorList)
                .apply(StructureTemplatePool.Projection.RIGID);
        if (piece instanceof WaystoneStructurePoolElement element) {
            element.waystones$setIsWaystone(true);
        }
        StructureTemplatePool pool = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL)
                .getOptional(Identifier.withDefaultNamespace(villagePiece))
                .orElse(null);
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
