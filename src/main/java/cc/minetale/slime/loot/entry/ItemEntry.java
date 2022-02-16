package cc.minetale.slime.loot.entry;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ItemEntry extends LootEntry {
    private NamespaceID name;

    @JsonCreator
    protected ItemEntry(NamespaceID name) {
        super(EntryType.ITEM);
        this.name = name;
    }

    @Override
    public @Nullable List<ItemStack> generateLoot(LootContext ctx) {
        if(!matchesConditions(ctx)) { return null; }

        var material = Material.fromNamespaceId(this.name);
        if(material == null) { return null; }

        var itemStack = ItemStack.of(material);

        List<ItemStack> fromThis = new ArrayList<>(List.of(itemStack));
        return applyFunctions(ctx, fromThis);
    }
}
