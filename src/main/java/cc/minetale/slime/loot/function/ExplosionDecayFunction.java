package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ExplosionDecayFunction extends LootFunction {
    @JsonCreator
    protected ExplosionDecayFunction(List<LootPredicate> conditions) {
        super(FunctionType.EXPLOSION_DECAY, conditions);
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return loot; }

        var radius = blockCtx.getRadius();
        if(radius == null) { return loot; }

        return loot
                .stream()
                .filter(itemStack -> ThreadLocalRandom.current().nextDouble() < (1d / radius))
                .collect(Collectors.toList());
    }
}
