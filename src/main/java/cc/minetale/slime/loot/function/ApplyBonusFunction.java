package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

@Getter @Setter
public class ApplyBonusFunction extends LootFunction {
    private Enchantment enchantment;
    private Formula formula;
    private Parameters parameters;

    @JsonCreator
    protected ApplyBonusFunction(Enchantment enchantment, Formula formula, Parameters parameters, List<LootPredicate> conditions) {
        super(FunctionType.APPLY_BONUS, conditions);
        this.enchantment = enchantment;
        this.formula = formula;
        this.parameters = parameters;
    }

    @Override public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return loot; }

        final var tool = blockCtx.getTool();

        var meta = tool.getMeta();
        var level = meta.getEnchantmentMap().get(this.enchantment);
        if(level == null) { return loot; }

        return loot
                .stream()
                .map(itemStack -> {
                    IntUnaryOperator operator = IntUnaryOperator.identity();

                    switch(this.formula) {
                        case BINOMIAL_WITH_BONUS_COUNT -> {
                            var provider = new NumberProvider.Binomial(level + this.parameters.getExtra(), this.parameters.getProbability());
                            operator = previous -> previous + ((int) provider.get());
                        }

                        case UNIFORM_BONUS_COUNT -> {
                            var provider = new NumberProvider.Uniform(0, level * this.parameters.getBonusMultiplier());
                            operator = previous -> previous + ((int) provider.get());
                        }

                        case ORE_DROPS -> {
                            var provider = new NumberProvider.Uniform(0, level + 2f);
                            operator = previous -> itemStack.getAmount() * (Math.max(0, ((int) provider.get()) - 1) + 1);
                        }
                    }

                    return itemStack.withAmount(operator);
                })
                .collect(Collectors.toList());
    }

    public enum Formula {
        BINOMIAL_WITH_BONUS_COUNT,
        UNIFORM_BONUS_COUNT,
        ORE_DROPS;

        @JsonValue
        private final NamespaceID id = NamespaceID.from("minecraft", name().toLowerCase(Locale.ROOT));

        public NamespaceID asId() {
            return this.id;
        }

        @JsonCreator
        public static Formula fromId(NamespaceID id) {
            return Arrays.stream(Formula.values())
                    .filter(type -> Objects.equals(type.id, id))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Getter @Setter @AllArgsConstructor
    public static class Parameters {
        private int extra;
        private float probability;
        private float bonusMultiplier;
    }
}
