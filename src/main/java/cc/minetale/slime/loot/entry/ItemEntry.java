package cc.minetale.slime.loot.entry;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ItemEntry extends LootEntry {
    private Material material;

    @JsonCreator
    protected ItemEntry(@JsonProperty("name") Material material, int weight) {
        super(EntryType.ITEM, weight);
        this.material = material;
    }

    @Override
    public @Nullable List<ItemStack> generateLoot(LootContext ctx) {
        if(!matchesConditions(ctx)) { return null; }

        var itemStack = ItemStack.of(this.material);

        List<ItemStack> fromThis = new ArrayList<>(List.of(itemStack));
        return applyFunctions(ctx, fromThis);
    }
}
