package cc.minetale.slime.loot.entry;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.function.LootFunction;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public abstract class LootEntry {
    private final EntryType type;

    private final List<LootPredicate> conditions;
    private final List<LootFunction> functions;

    private final int weight;

    protected LootEntry(EntryType type, int weight) {
        this.type = type;

        this.conditions = Collections.synchronizedList(new ArrayList<>());
        this.functions = Collections.synchronizedList(new ArrayList<>());

        this.weight = weight;
    }

    public abstract @Nullable List<ItemStack> generateLoot(LootContext ctx);

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

    public LootEntry addCondition(LootPredicate condition) {
        this.conditions.add(condition);
        return this;
    }

    public LootEntry removeCondition(LootPredicate condition) {
        this.conditions.remove(condition);
        return this;
    }

    public LootEntry addFunction(LootFunction function) {
        this.functions.add(function);
        return this;
    }

    public LootEntry removePool(LootFunction function) {
        this.functions.remove(function);
        return this;
    }

    public static ItemEntry item(int weight, Material material) {
        return new ItemEntry(material, weight);
    }

    public static LootEntry tag() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootEntry lootTable() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootEntry group() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static AlternativesEntry alternatives(int weight, List<LootEntry> entries) {
        return new AlternativesEntry(entries, weight);
    }

    public static AlternativesEntry alternatives(int weight, LootEntry... entries) {
        return LootEntry.alternatives(weight, List.of(entries));
    }

    public static LootEntry sequence() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootEntry dynamic() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static EmptyEntry empty(int weight) {
        return new EmptyEntry(weight);
    }
}
