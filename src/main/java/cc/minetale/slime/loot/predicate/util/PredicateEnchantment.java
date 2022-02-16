package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;

@Getter @Setter @AllArgsConstructor
public final class PredicateEnchantment {
    private NamespaceID enchantment;
    private IntegerRangeProvider levels;

    public boolean test(ItemStack item) {
        var meta = item.getMeta();

        var enchantment = Enchantment.fromNamespaceId(this.enchantment);
        if(enchantment == null) { return false; }

        var level = meta.getEnchantmentMap().get(enchantment);
        return level != null && this.levels.isInRange(level);
    }
}
