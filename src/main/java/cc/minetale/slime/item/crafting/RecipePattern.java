package cc.minetale.slime.item.crafting;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket.Ingredient;

import java.util.*;

@Getter @Setter
public class RecipePattern {
    private static final Ingredient EMPTY_INGREDIENT = new Ingredient(Collections.singletonList(ItemStack.AIR));

    private int width;
    private int height;
    private String pattern;

    protected RecipePattern(int width, int height, String pattern) {
        this.width = width;
        this.height = height;
        this.pattern = pattern;
    }

    public static RecipePattern ofString(int width, int height, String pattern) {
        if(pattern.length() != width * height) { return null; }
        return new RecipePattern(width, height, pattern);
    }

    public List<Ingredient> apply(Map<Character, Ingredient> available) {
        List<Ingredient> ingredients = new LinkedList<>();

        for(var part : pattern.toCharArray()) {
            var ingredient = Objects.requireNonNullElse(available.get(part), EMPTY_INGREDIENT);
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    public void setPattern(String pattern) {
        if(pattern.length() != size()) { return; }
        this.pattern = pattern;
    }

    public void resize(int newWidth, int newHeight) {
        if(this.pattern.length() != newWidth * newHeight) { return; }
        this.width = newWidth;
        this.height = newHeight;
    }

    public int size() {
        return this.width * this.height;
    }
}
