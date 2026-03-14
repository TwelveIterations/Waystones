package net.blay09.mods.waystones.datagen;

import net.blay09.mods.balm.tags.BalmItemTags;
import net.blay09.mods.waystones.api.WaystoneType;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.api.SharestoneType;
import net.blay09.mods.waystones.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                ModBlocks.waystones.forEach((type, block) -> {
                    shaped(RecipeCategory.DECORATIONS, block)
                            .pattern(" S ")
                            .pattern("SWS")
                            .pattern("SSS")
                            .define('S', type.getIngredient())
                            .define('W', ModItems.warpStones.get(null))
                            .unlockedBy("has_warp_stone", has(ModItems.warpStones.get(null)))
                            .save(exporter);
                });

                shapeless(RecipeCategory.DECORATIONS, ModBlocks.waystones.get(WaystoneType.MOSSY))
                        .requires(ModBlocks.waystones.get(WaystoneType.ANDESITE))
                        .requires(Blocks.VINE, 3)
                        .unlockedBy("has_waystone", has(ModBlocks.waystones.get(WaystoneType.ANDESITE)))
                        .save(exporter, "waystones:mossy_waystone_from_vines");

                shapeless(RecipeCategory.DECORATIONS, ModBlocks.waystones.get(WaystoneType.MOSSY))
                        .requires(ModBlocks.waystones.get(WaystoneType.ANDESITE))
                        .requires(Blocks.MOSS_BLOCK, 3)
                        .unlockedBy("has_waystone", has(ModBlocks.waystones.get(WaystoneType.ANDESITE)))
                        .save(exporter, "waystones:mossy_waystone_from_moss_blocks");

                shaped(RecipeCategory.DECORATIONS, ModBlocks.warpPlate)
                        .pattern("SAS")
                        .pattern("AFA")
                        .pattern("SAS")
                        .define('F', Items.FLINT)
                        .define('A', Items.AMETHYST_SHARD)
                        .define('S', Blocks.STONE_BRICKS)
                        .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                        .save(exporter);

                shaped(RecipeCategory.DECORATIONS, ModItems.dormantShard, 1)
                        .pattern(" A ")
                        .pattern("AFA")
                        .pattern(" A ")
                        .define('F', Items.FLINT)
                        .define('A', Items.AMETHYST_SHARD)
                        .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                        .save(exporter);

                shaped(RecipeCategory.DECORATIONS, ModBlocks.portstones.get(null))
                        .pattern(" S ")
                        .pattern("SWS")
                        .pattern("BBB")
                        .define('B', Blocks.STONE_BRICKS)
                        .define('W', ModItems.warpStones.get(null))
                        .define('S', Blocks.STONE_BRICK_SLAB)
                        .unlockedBy("has_warp_stone", has(ModItems.warpStones.get(null)))
                        .save(exporter);

                createPortstoneRecipe(exporter, SharestoneType.COPPER);
                createPortstoneRecipe(exporter, SharestoneType.PRISMARINE);
                createPortstoneRecipe(exporter, SharestoneType.GOLD);
                createPortstoneRecipe(exporter, SharestoneType.DIAMOND);
                createPortstoneRecipe(exporter, SharestoneType.AMETHYST);
                createPortstoneRecipe(exporter, SharestoneType.LAPIS);
                createPortstoneRecipe(exporter, SharestoneType.EMERALD);
                createPortstoneRecipe(exporter, SharestoneType.REDSTONE);

                createSharestoneRecipe(exporter, SharestoneType.COPPER);
                createSharestoneRecipe(exporter, SharestoneType.PRISMARINE);
                createSharestoneRecipe(exporter, SharestoneType.GOLD);
                createSharestoneRecipe(exporter, SharestoneType.DIAMOND);
                createSharestoneRecipe(exporter, SharestoneType.AMETHYST);
                createSharestoneRecipe(exporter, SharestoneType.LAPIS);
                createSharestoneRecipe(exporter, SharestoneType.EMERALD);
                createSharestoneRecipe(exporter, SharestoneType.REDSTONE);

                shaped(RecipeCategory.DECORATIONS, ModItems.warpStones.get(null))
                        .pattern("AEA")
                        .pattern("EGE")
                        .pattern("AEA")
                        .define('G', Items.GOLD_INGOT)
                        .define('E', Items.ENDER_PEARL)
                        .define('A', Items.AMETHYST_SHARD)
                        .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                        .save(exporter);

                shaped(RecipeCategory.DECORATIONS, ModItems.warpScroll, 3)
                        .pattern("GIG")
                        .pattern("GAG")
                        .pattern("PPP")
                        .define('I', Items.INK_SAC)
                        .define('G', BalmItemTags.GOLD_NUGGETS)
                        .define('A', Items.AMETHYST_SHARD)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                        .save(exporter);

                shaped(RecipeCategory.DECORATIONS, ModItems.blankScroll, 3)
                        .pattern("GFG")
                        .pattern("PPP")
                        .define('F', Items.FEATHER)
                        .define('G', BalmItemTags.GOLD_NUGGETS)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_paper", has(Items.PAPER))
                        .save(exporter);

                shaped(RecipeCategory.DECORATIONS, ModItems.returnScroll, 3)
                        .pattern("GIG")
                        .pattern("PPP")
                        .define('I', Items.INK_SAC)
                        .define('G', BalmItemTags.GOLD_NUGGETS)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_paper", has(Items.PAPER))
                        .save(exporter);
            }

            private void createSharestoneRecipe(RecipeOutput exporter, SharestoneType type) {
                final var sharestone = ModBlocks.sharestones.get(type);
                if (sharestone == null) {
                    return;
                }

                shaped(RecipeCategory.DECORATIONS, sharestone)
                        .pattern("SSS")
                        .pattern("DWD")
                        .pattern("SSS")
                        .define('S', Blocks.STONE_BRICKS)
                        .define('W', ModItems.warpStones.get(null))
                        .define('D', type.getIngredient())
                        .unlockedBy("has_warp_stone", has(ModItems.warpStones.get(null)))
                        .save(exporter);
            }

            private void createPortstoneRecipe(RecipeOutput exporter, SharestoneType type) {
                final var portstone = ModBlocks.portstones.get(type);
                if (portstone == null) {
                    return;
                }

                shaped(RecipeCategory.DECORATIONS, portstone)
                        .pattern("DSD")
                        .pattern("SWS")
                        .pattern("BBB")
                        .define('B', Blocks.STONE_BRICKS)
                        .define('W', ModItems.warpStones.get(null))
                        .define('S', Blocks.STONE_BRICK_SLAB)
                        .define('D', type.getIngredient())
                        .unlockedBy("has_warp_stone", has(ModItems.warpStones.get(null)))
                        .save(exporter);
            }

        };
    }

    @Override
    public String getName() {
        return "waystones";
    }
}