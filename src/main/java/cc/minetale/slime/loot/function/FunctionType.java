package cc.minetale.slime.loot.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import net.minestom.server.utils.NamespaceID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum FunctionType {
    APPLY_BONUS(ApplyBonusFunction.class),
    COPY_NAME(CopyNameFunction.class),
    COPY_NBT(CopyNBTFunction.class),
    COPY_STATE(CopyStateFunction.class),
    ENCHANT_RANDOMLY(EnchantRandomlyFunction.class),
    ENCHANT_WITH_LEVELS(EnchantWithLevelsFunction.class),
    EXPLORATION_MAP(null),
    EXPLOSION_DECAY(ExplosionDecayFunction.class),
    FURNACE_SMELT(null),
    FILL_PLAYER_HEAD(null),
    LIMIT_COUNT(null),
    LOOTING_ENCHANT(null),
    SET_ATTRIBUTES(null),
    SET_BANNER_PATTERN(null),
    SET_CONTENTS(SetContentsFunction.class),
    SET_COUNT(SetCountFunction.class),
    SET_DAMAGE(SetDamageFunction.class),
    SET_ENCHANTMENTS(null),
    SET_LOOT_TABLE(null),
    SET_LORE(null),
    SET_NAME(null),
    SET_NBT(null),
    SET_POTION(null),
    SET_STEW_EFFECT(SetStewEffectFunction.class);

    @JsonValue private final NamespaceID id;
    @JsonIgnore @Getter private final Class<? extends LootFunction> functionClass;

    FunctionType(Class<? extends LootFunction> functionClass) {
        this.id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));
        this.functionClass = functionClass;
    }

    public NamespaceID asId() {
        return this.id;
    }

    @JsonCreator
    public static FunctionType fromId(NamespaceID id) {
        return Arrays.stream(FunctionType.values())
                .filter(type -> Objects.equals(type.id, id))
                .findFirst()
                .orElse(null);
    }
}
