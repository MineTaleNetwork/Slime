package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class SurvivesExplosionPredicate extends LootPredicate {
    @JsonCreator
    protected SurvivesExplosionPredicate() {
        super(PredicateType.SURVIVES_EXPLOSION);
    }

    @Override public boolean test(LootContext ctx) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return false; }

        var radius = blockCtx.getRadius();
        return radius == null || ThreadLocalRandom.current().nextDouble() < (1d / radius);
    }
}
