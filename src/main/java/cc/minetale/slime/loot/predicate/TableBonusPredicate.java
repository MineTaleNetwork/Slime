package cc.minetale.slime.loot.predicate;

import cc.minetale.slime.loot.context.LootContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class TableBonusPredicate extends LootPredicate {
    private NamespaceID enchantment;
    private final List<Double> chances;

    @JsonCreator
    protected TableBonusPredicate(NamespaceID enchantment, List<Double> chances) {
        super(PredicateType.TABLE_BONUS);
        this.enchantment = enchantment;

        this.chances = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(chances, Collections.emptyList())));
    }

    @Override public boolean test(LootContext ctx) {
        if(!(ctx instanceof LootContext.BlockCtx blockCtx)) { return false; }

        var tool = blockCtx.getTool();
        var enchantment = Enchantment.fromNamespaceId(this.enchantment);
        if(enchantment == null) { return false; }

        var level = tool.getMeta().getEnchantmentMap().get(enchantment);
        if(level == null) { return false; }

        var chance = this.chances.get(level);
        return ThreadLocalRandom.current().nextDouble() < chance;
    }
}
