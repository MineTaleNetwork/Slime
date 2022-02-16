package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.List;

public class AlternativePredicate extends LogicalPredicate {
    @JsonCreator
    protected AlternativePredicate(List<LootPredicate> terms) {
        super(terms, PredicateType.ALTERNATIVE);
    }

    protected AlternativePredicate(LootPredicate... terms) {
        this(Arrays.asList(terms));
    }

    protected AlternativePredicate(LootPredicate term) {
        this(List.of(term));
    }

    @Override
    public boolean test(LootContext ctx) {
        for(LootPredicate term : getTerms()) {
            if(term.test(ctx))
                return true;
        }

        return false;
    }
}
