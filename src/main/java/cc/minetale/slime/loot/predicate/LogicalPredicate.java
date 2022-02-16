package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class LogicalPredicate extends LootPredicate {
    private final List<LootPredicate> terms;

    protected LogicalPredicate(List<LootPredicate> terms, PredicateType condition) {
        super(condition);
        this.terms = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(terms, Collections.emptyList())));
    }

    @Override
    public boolean test(LootContext ctx) {
        return this.terms
                .stream()
                .allMatch(term -> term.test(ctx));
    }
}
