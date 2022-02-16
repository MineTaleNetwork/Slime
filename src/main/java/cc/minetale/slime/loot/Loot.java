package cc.minetale.slime.loot;

import cc.minetale.slime.loot.context.ContextType;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.function.LootFunction;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Loot {
    private final ContextType type;
    private final List<LootPool> pools;
    private final List<LootFunction> functions;

    @JsonCreator
    protected Loot(ContextType type, List<LootPool> pools, List<LootFunction> functions) {
        this.type = type;
        this.pools = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(pools, Collections.emptyList())));

        this.functions = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(functions, Collections.emptyList())));
    }

    public Loot(ContextType type) {
        this(type, null, null);
    }

    public void generateLoot(List<ItemStack> generated, LootContext ctx) {
        List<ItemStack> fromThis = new ArrayList<>();
        for(var pool : this.pools) {
            var fromPool = pool.generateLoot(ctx);
            if(fromPool != null)
                fromThis.addAll(fromPool);
        }
        generated.addAll(applyFunctions(ctx, fromThis));
    }

    public List<ItemStack> applyFunctions(LootContext ctx, List<ItemStack> loot) {
        for(var function : this.functions)
            loot = function.apply(ctx, loot);

        return loot;
    }
}
