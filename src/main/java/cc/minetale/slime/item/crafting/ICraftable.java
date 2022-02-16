package cc.minetale.slime.item.crafting;

import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;
import net.minestom.server.recipe.Recipe;
import net.minestom.server.recipe.ShapedRecipe;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ICraftable {
    //TODO Multiple patterns break the recipes (low priority)
    List<RecipePattern> getPatterns();
    Map<Character, Ingredient> getIngredients();

    List<Recipe> getRecipes(Function<RecipeInfo, ShapedRecipe> provider);

    //TODO Other recipe types e.g. ShapelessRecipe
}
