package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class SetContentsFunction extends LootFunction {
    private final List<LootEntry> entries;
    private NamespaceID type;

    @JsonCreator
    protected SetContentsFunction(List<LootEntry> entries, NamespaceID type, List<LootPredicate> conditions) {
        super(FunctionType.SET_CONTENTS, conditions);
        this.type = type;

        this.entries = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(entries, Collections.emptyList())));
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        if(!(ctx instanceof LootContext.BlockCtx)) { return loot; }
        return loot; //TODO Create a universal tag for containers
    }
}
