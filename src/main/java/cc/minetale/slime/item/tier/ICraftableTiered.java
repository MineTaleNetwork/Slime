package cc.minetale.slime.item.tier;

import cc.minetale.slime.item.ITier;
import cc.minetale.slime.item.crafting.ICraftable;
import cc.minetale.slime.item.crafting.RecipeInfo;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;
import net.minestom.server.recipe.Recipe;
import net.minestom.server.recipe.ShapedRecipe;

import java.util.*;
import java.util.function.Function;

public interface ICraftableTiered extends ICraftable, ITieredItem {
    default List<Recipe> getRecipesForTier(ITier tier, Function<RecipeInfo, ShapedRecipe> provider) {
        List<Recipe> recipes = new LinkedList<>();
        Map<ITier, Set<ITierItem>> tiers = getItemsGroupedByTier();

        for(var pattern : getPatterns()) {
            Set<ITierItem> results = tiers.get(tier);
            if(results == null || results.isEmpty()) { return null; }

            Set<Material> possibleMaterials = tier.getMaterials();
            if(possibleMaterials == null || possibleMaterials.isEmpty()) { return null; }

            List<Ingredient> ingredients = pattern.apply(getTierIngredients(tier));

            for(var result : results) {
                var id = result.getId();
                var info = new RecipeInfo(
                        id.asString(),
                        pattern.getWidth(), pattern.getHeight(),
                        "",
                        ingredients, ItemStack.of(result.getMaterial()));

                recipes.add(provider.apply(info));
            }
        }

        return recipes;
    }

    default List<Recipe> getRecipes(Function<RecipeInfo, ShapedRecipe> provider) {
        List<Recipe> recipes = new LinkedList<>();

        for(var tier : getPossibleTiers()) {
            List<Recipe> tierRecipes = getRecipesForTier(tier, provider);
            if(tierRecipes == null) { continue; }

            recipes.addAll(tierRecipes);
        }

        return recipes;
    }

    default Map<Character, Ingredient> getTierIngredients(ITier tier) {
        List<ItemStack> items = new ArrayList<>();
        for(Material material : tier.getMaterials()) {
            ItemStack item = ItemStack.of(material);
            items.add(item);
        }

        return new HashMap<>(Map.of('i', new Ingredient(items)));
    }

    default Map<Character, Ingredient> getIngredients() {
        List<ItemStack> itemStacks = new LinkedList<>();
        for(Set<ITierItem> items : getItemsGroupedByTier().values()) {
            for(ITierItem item : items) {
                var material = item.getMaterial();
                ItemStack itemStack = ItemStack.of(material);
                itemStacks.add(itemStack);
            }
        }

        return Collections.singletonMap('i', new Ingredient(itemStacks));
    }
}
