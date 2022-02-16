package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class InvertedPredicate extends LogicalPredicate {
    protected InvertedPredicate(List<LootPredicate> terms) {
        super(terms, PredicateType.INVERTED);
    }

    protected InvertedPredicate(LootPredicate... terms) {
        this(Arrays.asList(terms));
    }

    @JsonCreator
    protected InvertedPredicate(@JsonProperty("term") LootPredicate term) {
        this(List.of(term));
    }

    @Override
    public boolean test(LootContext ctx) {
        return !this.getTerms()
                .stream()
                .allMatch(term -> test(ctx));
    }
}
