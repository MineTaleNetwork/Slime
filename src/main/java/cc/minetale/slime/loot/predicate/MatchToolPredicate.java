package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.util.PredicateItem;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatchToolPredicate extends LootPredicate {
    private PredicateItem predicate;

    @JsonCreator
    protected MatchToolPredicate(PredicateItem predicate) {
        super(PredicateType.MATCH_TOOL);
        this.predicate = predicate;
    }

    @Override
    public boolean test(LootContext ctx) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return false; } //Documentation suggests at the possibility of other contexts?

        var tool = blockCtx.getTool();
        return this.predicate.test(tool);
    }
}
