package cc.minetale.slime.loot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum NumberProviderType {
    CONSTANT(NumberProvider.Constant.class),
    UNIFORM(NumberProvider.Uniform.class),
    BINOMIAL(NumberProvider.Binomial.class),
    SCORE(null);

    @JsonValue private final NamespaceID id;
    @JsonIgnore @Getter private final Class<? extends NumberProvider> providerClass;

    NumberProviderType(Class<? extends NumberProvider> providerClass) {
        this.id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));
        this.providerClass = providerClass;
    }

    public NamespaceID asId() {
        return this.id;
    }

    @JsonCreator
    public static NumberProviderType fromId(NamespaceID id) {
        return Arrays.stream(NumberProviderType.values())
                .filter(type -> Objects.equals(type.id, id))
                .findFirst()
                .orElse(null);
    }
}
