package cc.minetale.slime.item.crafting;

import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;

import java.util.List;

public record RecipeInfo(String id, int width, int height, String group, List<Ingredient> ingredients, ItemStack result) {
}
