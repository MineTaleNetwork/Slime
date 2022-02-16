package cc.minetale.slime.item.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;
import net.minestom.server.recipe.Recipe;
import net.minestom.server.recipe.ShapedRecipe;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ISingleRecipe extends ICraftable {
    ItemStack getRecipeResult();

    default List<Recipe> getRecipes(Function<RecipeInfo, ShapedRecipe> provider) {
        List<Recipe> recipes = new LinkedList<>();

        var result = getRecipeResult();
        var material = result.getMaterial();
        var id = material.namespace();

        Map<Character, Ingredient> available = getIngredients();
        for(var pattern : getPatterns()) {
            List<Ingredient> ingredients = pattern.apply(available);

            var info = new RecipeInfo(
                    id.asString(),
                    pattern.getWidth(), pattern.getHeight(),
                    "",
                    ingredients, result);
            var recipe = provider.apply(info);

            for(var ingredient : ingredients)
                recipe.addIngredient(ingredient);

            recipes.add(recipe);
        }

        return recipes;
    }
}
