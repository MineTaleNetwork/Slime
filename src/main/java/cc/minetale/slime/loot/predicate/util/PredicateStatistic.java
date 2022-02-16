package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Getter @Setter @AllArgsConstructor
public class PredicateStatistic {
    private Type type;
    private String stat; //TODO Parse
    private IntegerRangeProvider value;

    private enum Type {
        CUSTOM,
        CRATED,
        USED,
        BROKEN,
        MINED,
        KILLED,
        PICKED_UP,
        DROPPED,
        KILLED_BY;

        @JsonValue
        private final NamespaceID id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));

        public NamespaceID asId() {
            return this.id;
        }

        @JsonCreator
        public static Type fromId(NamespaceID id) {
            return Arrays.stream(Type.values())
                    .filter(type -> Objects.equals(type.id, id))
                    .findFirst()
                    .orElse(null);
        }
    }
}
