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
public class SetDamageFunction extends LootFunction {
    private NumberProvider damage;
    private boolean add;

    @JsonCreator
    protected SetDamageFunction(NumberProvider damage, boolean add, List<LootPredicate> conditions) {
        super(FunctionType.SET_DAMAGE, conditions);
        this.damage = damage;
        this.add = add;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    var currentMeta = itemStack.getMeta();

                    var maxDamage = itemStack.getMaterial()
                            .registry()
                            .maxDamage();

                    return itemStack.withMeta(meta -> {
                        var damage = Math.round(maxDamage * this.damage.get());
                        if(this.add) {
                            damage += currentMeta.getDamage();
                        }

                        return meta.damage(damage);
                    });
                })
                .collect(Collectors.toList());
    }
}
