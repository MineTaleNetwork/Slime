package cc.minetale.slime.loot.entry;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class AlternativesEntry extends LootEntry {
    private List<LootEntry> children;

    @JsonCreator
    protected AlternativesEntry(List<LootEntry> children, int weight) {
        super(EntryType.ALTERNATIVES, weight);
        this.children = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(children, Collections.emptyList())));
    }

    @Override
    public @Nullable List<ItemStack> generateLoot(LootContext ctx) {
        if(!matchesConditions(ctx)) { return null; }

        for(var child : children) {
            List<ItemStack> fromThis = child.generateLoot(ctx);
            if(fromThis != null) {
                fromThis = applyFunctions(ctx, fromThis);
                return fromThis;
            }
        }

        return null;
    }
}
