package net.blay09.mods.waystones.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.waystones.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Map;

public class WarpPlateRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final ItemStack resultItem;
    private final Ingredient primaryIngredient;
    private final NonNullList<Ingredient> secondaryIngredients;
    private final NonNullList<Ingredient> combinedIngredients;

    public WarpPlateRecipe(ResourceLocation id, ItemStack resultItem, Ingredient primaryIngredient, NonNullList<Ingredient> secondaryIngredients) {
        this.id = id;
        this.resultItem = resultItem;

        this.primaryIngredient = primaryIngredient;
        this.secondaryIngredients = secondaryIngredients;

        this.combinedIngredients = NonNullList.createWithCapacity(5);
        this.combinedIngredients.add(primaryIngredient);
        this.combinedIngredients.addAll(secondaryIngredients);
    }

    @Override
    public boolean matches(Container inventory, Level level) {
        // Short-circuit if the primary ingredient is not present to ensure it's actually in the right slot and avoid unnecessary processing
        if (!primaryIngredient.test(inventory.getItem(0))) {
            return false;
        }

        StackedContents stackedContents = new StackedContents();
        int foundInputs = 0;
        // canCraft uses getIngredients, so we need to include the primary ingredient here as well
        for (int i = 0; i < combinedIngredients.size(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                foundInputs++;
                stackedContents.accountStack(itemStack, 1);
            }
        }
        return foundInputs == combinedIngredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryAccess) {
        return this.resultItem.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.resultItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.combinedIngredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String getGroup() {
        return ModRecipes.WARP_PLATE_RECIPE_GROUP;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.warpPlate);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.warpPlateRecipeSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.warpPlateRecipeType;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    static class Serializer implements RecipeSerializer<WarpPlateRecipe> {

        @Override
        public WarpPlateRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            Ingredient primaryIngredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "primary"));
            NonNullList<Ingredient> secondaryIngredients = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "secondary"));
            if (secondaryIngredients.isEmpty()) {
                throw new JsonParseException("No secondary ingredients for warp plate recipe");
            } else if (secondaryIngredients.size() > 4) {
                throw new JsonParseException("Too many secondary ingredients for shapeless recipe");
            } else {
                ItemStack resultItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                return new WarpPlateRecipe(id, resultItem, primaryIngredient, secondaryIngredients);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for(int i = 0; i < jsonArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i), false);
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }

            return ingredients;
        }

        @Override
        public WarpPlateRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var resultItem = buf.readItem();
            final var primaryIngredient = Ingredient.fromNetwork(buf);
            final var secondaryCount = buf.readVarInt();
            final NonNullList<Ingredient> secondaryIngredients = NonNullList.createWithCapacity(secondaryCount);
            for (int i = 0; i < secondaryCount; i++) {
                secondaryIngredients.add(Ingredient.fromNetwork(buf));
            }
            return new WarpPlateRecipe(id, resultItem, primaryIngredient, secondaryIngredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WarpPlateRecipe recipe) {
            buf.writeItem(recipe.resultItem);
            recipe.primaryIngredient.toNetwork(buf);
            buf.writeVarInt(recipe.secondaryIngredients.size());
            for (Ingredient ingredient : recipe.secondaryIngredients) {
                ingredient.toNetwork(buf);
            }
        }
    }

}
