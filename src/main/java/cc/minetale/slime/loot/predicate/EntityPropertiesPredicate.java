package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.util.PredicateEntityProps;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Getter @Setter
public class EntityPropertiesPredicate extends LootPredicate {
    private Type entity;
    private PredicateEntityProps predicate;

    @JsonCreator
    protected EntityPropertiesPredicate(Type entity, PredicateEntityProps predicate) {
        super(PredicateType.ENTITY_PROPERTIES);
        this.entity = entity;
        this.predicate = predicate;
    }

    @Override
    public boolean test(LootContext ctx) {
        if(!(ctx instanceof LootContext.EntityCtx entityCtx)) { return false; }

        var killer = entityCtx.getKiller();
        Entity toCheck = null;
        switch(this.entity) {
            case THIS -> toCheck = entityCtx.getEntity();
            case KILLER -> toCheck = killer;
            case KILLER_PLAYER -> {
                if(!(killer instanceof Player)) { return false; }
                toCheck = killer;
            }
        }
        if(toCheck == null) { return false; }

        return this.predicate.test(toCheck, killer);
    }

    public enum Type {
        THIS,
        KILLER,
        KILLER_PLAYER;

        @JsonValue private final String id = name().toLowerCase(Locale.ROOT);

        public String asId() {
            return this.id;
        }

        @JsonCreator
        public static EntityPropertiesPredicate.Type fromId(String id) {
            return Arrays.stream(EntityPropertiesPredicate.Type.values())
                    .filter(type -> Objects.equals(type.id, id))
                    .findFirst()
                    .orElse(null);
        }
    }
}
