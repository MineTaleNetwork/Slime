package cc.minetale.slime.loot;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.function.LootFunction;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;

import java.util.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter @Setter
public class LootPool {
    private NumberProvider rolls;
    private NumberProvider bonusRolls; //TODO Make use

    private final List<LootEntry> entries;

    private final List<LootPredicate> conditions;
    private final List<LootFunction> functions;

    @JsonCreator
    private LootPool(NumberProvider rolls, NumberProvider bonusRolls, List<LootEntry> entries, List<LootPredicate> conditions, List<LootFunction> functions) {
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;

        this.entries = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(entries, Collections.emptyList())));

        this.conditions = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(conditions, Collections.emptyList())));

        this.functions = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(functions, Collections.emptyList())));
    }

    public LootPool(NumberProvider rolls, NumberProvider bonusRolls) {
        this(rolls, bonusRolls, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public List<ItemStack> generateLoot(LootContext ctx) {
        List<ItemStack> fromThis = new LinkedList<>();

        if(!matchesConditions(ctx))
            return fromThis;

        var toRoll = (int) Math.floor(this.rolls.get());
        for(int i = 0; i < toRoll; i++) {
            var entry = CollectionsUtil.weightedRandom(this.entries, LootEntry::getWeight);

            List<ItemStack> fromEntry = entry.generateLoot(ctx);
            if(fromEntry != null)
                fromThis.addAll(fromEntry);
        }

        return applyFunctions(ctx, fromThis);
    }

    public boolean matchesConditions(LootContext ctx) {
        return this.conditions
                .stream()
                .allMatch(condition -> condition.test(ctx));
    }

    public List<ItemStack> applyFunctions(LootContext ctx, List<ItemStack> loot) {
        for(var function : this.functions)
            loot = function.apply(ctx, loot);

        return loot;
    }
}
