package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class SetCountFunction extends LootFunction {
    private NumberProvider count;
    private boolean add;

    @JsonCreator
    protected SetCountFunction(NumberProvider count, boolean add, List<LootPredicate> conditions) {
        super(FunctionType.SET_COUNT, conditions);
        this.count = count;
        this.add = add;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    var amount = (int) Math.floor(this.count.get());
                    if(this.add)
                        amount += itemStack.getAmount();

                    return itemStack.withAmount(amount);
                })
                .collect(Collectors.toList());
    }
}
