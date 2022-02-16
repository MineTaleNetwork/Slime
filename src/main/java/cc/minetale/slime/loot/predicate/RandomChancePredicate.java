package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class RandomChancePredicate extends LootPredicate {
    @Setter private float chance;

    @JsonCreator
    protected RandomChancePredicate(float chance) {
        super(PredicateType.RANDOM_CHANCE);
        this.chance = chance;
    }

    @Override public boolean test(LootContext ctx) {
        return ThreadLocalRandom.current().nextFloat() < chance;
    }
}
