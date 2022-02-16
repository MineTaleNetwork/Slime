package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.util.PredicateEntityProps;
import cc.minetale.slime.loot.predicate.util.PredicateItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.List;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter @AllArgsConstructor
public abstract class LootPredicate {
    @JsonProperty("condition") private final PredicateType type;

    public abstract boolean test(LootContext ctx);

    public static AlternativePredicate alternative(List<LootPredicate> terms) {
        return new AlternativePredicate(terms);
    }

    public static BlockStatePropertyPredicate blockStateProperty(NamespaceID block, Map<String, String> properties) {
        return new BlockStatePropertyPredicate(block, properties);
    }

    public static LootPredicate damageSourceProperties() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static EntityPropertiesPredicate entityProperties(EntityPropertiesPredicate.Type type, PredicateEntityProps properties) {
        return new EntityPropertiesPredicate(type, properties);
    }

    public static LootPredicate entityScores() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static InvertedPredicate inverted(LootPredicate... terms) {
        return new InvertedPredicate(terms);
    }

    public static LootPredicate killedByPlayer() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootPredicate locationCheck() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static MatchToolPredicate matchTool(PredicateItem item) {
        return new MatchToolPredicate(item);
    }

    public static LootPredicate randomChance() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootPredicate randomChanceWithLooting() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootPredicate reference() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static SurvivesExplosionPredicate survivesExplosion() {
        return new SurvivesExplosionPredicate();
    }

    public static TableBonusPredicate tableBonus(NamespaceID enchantment, List<Double> chances) {
        return new TableBonusPredicate(enchantment, chances);
    }

    public static LootPredicate timeCheck() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootPredicate valueCheck() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static LootPredicate weatherCheck() {
        //TODO
        throw new UnsupportedOperationException();
    }
}
