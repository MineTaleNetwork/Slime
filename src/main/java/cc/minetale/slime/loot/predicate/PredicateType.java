package cc.minetale.slime.loot.predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum PredicateType {
    ALTERNATIVE(AlternativePredicate.class),
    BLOCK_STATE_PROPERTY(BlockStatePropertyPredicate.class),
    DAMAGE_SOURCE_PROPERTIES(null),
    ENTITY_PROPERTIES(EntityPropertiesPredicate.class),
    ENTITY_SCORES(null),
    INVERTED(InvertedPredicate.class),
    KILLED_BY_PLAYER(null),
    LOCATION_CHECK(LocationCheckPredicate.class),
    MATCH_TOOL(MatchToolPredicate.class),
    RANDOM_CHANCE(RandomChancePredicate.class),
    RANDOM_CHANCE_WITH_LOOTING(null),
    REFERENCE(null),
    SURVIVES_EXPLOSION(SurvivesExplosionPredicate.class),
    TABLE_BONUS(TableBonusPredicate.class),
    TIME_CHECK(null),
    VALUE_CHECK(null),
    WEATHER_CHECK(null);

    @JsonValue private final NamespaceID id;
    @JsonIgnore @Getter private final Class<? extends LootPredicate> predicateClass;

    PredicateType(Class<? extends LootPredicate> predicateClass) {
        this.id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));
        this.predicateClass = predicateClass;
    }

    public NamespaceID asId() {
        return this.id;
    }

    @JsonCreator
    public static PredicateType fromId(NamespaceID id) {
        return Arrays.stream(PredicateType.values())
                .filter(type -> Objects.equals(type.id, id))
                .findFirst()
                .orElse(null);
    }

}
