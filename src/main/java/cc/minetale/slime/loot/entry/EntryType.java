package cc.minetale.slime.loot.entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum EntryType {
    ITEM(ItemEntry.class),
    TAG(null),
    LOOT_TABLE(null),
    GROUP(null),
    ALTERNATIVES(AlternativesEntry.class),
    SEQUENCE(null),
    DYNAMIC(null),
    EMPTY(null);

    @JsonValue
    private final NamespaceID id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));

    @JsonIgnore @Getter private final Class<? extends LootEntry> entryClass;

    EntryType(Class<? extends LootEntry> entryClass) {
        this.entryClass = entryClass;
    }

    public NamespaceID asId() {
        return this.id;
    }

    @JsonCreator
    public static EntryType fromId(NamespaceID id) {
        return Arrays.stream(EntryType.values())
                .filter(type -> Objects.equals(type.id, id))
                .findFirst()
                .orElse(null);
    }
}
