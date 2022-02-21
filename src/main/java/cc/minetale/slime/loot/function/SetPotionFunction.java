package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.metadata.PotionMeta;
import net.minestom.server.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class SetPotionFunction extends LootFunction {
    private PotionType potionType;

    @JsonCreator
    protected SetPotionFunction(@JsonProperty("id") PotionType potionType, List<LootPredicate> conditions) {
        super(FunctionType.SET_POTION, conditions);
        this.potionType = potionType;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    return itemStack.withMeta(meta -> {
                        if(!(meta instanceof PotionMeta.Builder potionMeta)) { return meta; }
                        return potionMeta.potionType(this.potionType);
                    });
                })
                .collect(Collectors.toList());
    }
}
