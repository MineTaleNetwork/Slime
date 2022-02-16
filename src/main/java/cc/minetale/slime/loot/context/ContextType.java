package cc.minetale.slime.loot.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum ContextType {
    EMPTY(LootContext.Empty.class),
    CHEST(LootContext.ChestCtx.class),
    COMMAND(LootContext.CommandCtx.class),
    SELECTOR(LootContext.SelectorCtx.class),
    FISHING(LootContext.FishingCtx.class),
    ENTITY(LootContext.EntityCtx.class),
    GIFT(LootContext.GiftCtx.class),
    BARTER(LootContext.BarterCtx.class),
    ADVANCEMENT_REWARD(LootContext.AdvancementRewardCtx.class),
    ADVANCEMENT_ENTITY(LootContext.AdvancementEntityCtx.class),
    GENERIC(LootContext.GenericCtx.class),
    BLOCK(LootContext.BlockCtx.class);

    @JsonValue
    private final NamespaceID id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));

    @JsonIgnore @Getter private final Class<? extends LootContext> contextClass;

    ContextType(Class<? extends LootContext> contextClass) {
        this.contextClass = contextClass;
    }

    public NamespaceID asId() {
        return this.id;
    }

    @JsonCreator
    public static ContextType fromId(NamespaceID id) {
        return Arrays.stream(ContextType.values())
                .filter(type -> Objects.equals(type.id, id))
                .findFirst()
                .orElse(null);
    }
}
