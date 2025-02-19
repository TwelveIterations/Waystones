package net.blay09.mods.waystones.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public class JEIAddon implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Waystones.MOD_ID, "jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(WarpPlateJeiRecipeCategory.TYPE, List.of(new AttunedShardJeiRecipe()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new WarpPlateJeiRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
