package cc.minetale.slime.loot.entry;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Getter @Setter
public class EmptyEntry extends LootEntry {
    @JsonCreator
    protected EmptyEntry(int weight) {
        super(EntryType.EMPTY, weight);
    }

    @Override
    public @Nullable List<ItemStack> generateLoot(LootContext ctx) {
        if(!matchesConditions(ctx)) { return null; }
        return Collections.emptyList();
    }
}
