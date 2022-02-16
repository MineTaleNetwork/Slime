package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.potion.TimedPotion;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class SetStewEffectFunction extends LootFunction {
    private List<Effect> effects;

    @JsonCreator
    protected SetStewEffectFunction(List<Effect> effects, List<LootPredicate> conditions) {
        super(FunctionType.SET_STEW_EFFECT, conditions);
        this.effects = effects;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    return itemStack.withMeta(meta -> {
                        //TODO Make meta class for suspicious stew
                        return meta;
                    });
                })
                .collect(Collectors.toList());
    }

    @Getter @AllArgsConstructor
    public static class Effect {
        private final NamespaceID type;
        private final NumberProvider duration;

        public @Nullable TimedPotion toTimedPotion() {
            var effect = PotionEffect.fromNamespaceId(this.type);
            if(effect == null) { return null; }

            var duration = Math.round(this.duration.get());

            return new TimedPotion(new Potion(effect, (byte) 1, duration), System.currentTimeMillis());
        }
    }
}
