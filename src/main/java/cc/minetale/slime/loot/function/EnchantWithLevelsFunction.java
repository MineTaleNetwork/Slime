package cc.minetale.slime.loot.function;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.mlib.util.MathUtil;
import cc.minetale.slime.item.base.IItem;
import cc.minetale.slime.item.enchant.IEnchantable;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.util.NumberProvider;
import cc.minetale.slime.utils.EnchantmentUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.EnchantedBookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static cc.minetale.slime.Slime.ITEM_MANAGER;

@Getter @Setter
public class EnchantWithLevelsFunction extends LootFunction {
    private final boolean treasure;
    private final NumberProvider levels;

    @JsonCreator
    protected EnchantWithLevelsFunction(boolean treasure, NumberProvider levels, List<LootPredicate> conditions) {
        super(FunctionType.ENCHANT_WITH_LEVELS, conditions);
        this.treasure = treasure;
        this.levels = levels;
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    IItem item = ITEM_MANAGER.getItemsFromStack(itemStack).stream()
                            .findFirst()
                            .orElse(null);

                    if(!(item instanceof IEnchantable enchantable)) { return null; }

                    Map<Enchantment, Short> enchantments = new HashMap<>();

                    var rng = ThreadLocalRandom.current();
                    var level = Math.round(this.levels.get());

                    var isBook = itemStack.getMaterial() == Material.BOOK;
                    int i = enchantable.getEnchantability();
                    if (i > 0) {
                        level += 1 + rng.nextInt(i / 4 + 1) + rng.nextInt(i / 4 + 1);

                        float f = (rng.nextFloat() + rng.nextFloat() - 1.0f) * 0.15f;
                        var power = MathUtil.clamp(Math.round((float) level + (float) level * f), 1, Integer.MAX_VALUE);

                        Map<Enchantment, Short> enchants = new HashMap<>();

                        enchants:
                        for(Enchantment enchantment : Enchantment.values()) {
                            var registry = enchantment.registry();
                            if(registry.isTreasureOnly() && !this.treasure ||
                                    !registry.isDiscoverable() ||
                                    !enchantable.getAllEnchantments().contains(enchantment) && !isBook) continue;

                            for (int j = (int) registry.maxLevel(); j > 0; --j) {
                                if (power < EnchantmentUtil.getMinPower(enchantment, j) || power > EnchantmentUtil.getMaxPower(enchantment, j)) continue;
                                enchants.put(enchantment, (short) j);
                                continue enchants;
                            }
                        }

                        if (!enchants.isEmpty()) {
                            var enchant = CollectionsUtil.randomEntry(enchants);
                            if(enchant != null)
                                enchantments.put(enchant.getKey(), enchant.getValue());

                            while (rng.nextInt(50) <= level) {
                                if (!enchantments.isEmpty())
                                    EnchantmentUtil.removeConflicts(enchants, CollectionsUtil.last(enchantments.entrySet()));

                                if (enchants.isEmpty()) break;
                                enchant = CollectionsUtil.randomEntry(enchants);
                                if(enchant != null) { enchantments.put(enchant.getKey(), enchant.getValue()); }

                                level /= 2;
                            }
                        }
                    }

                    if(isBook)
                        itemStack = ItemStack.of(Material.ENCHANTED_BOOK, itemStack.getAmount());

                    return itemStack.withMeta(meta -> {
                        if(isBook) {
                            if(meta instanceof EnchantedBookMeta.Builder bookMeta)
                                bookMeta.enchantments(enchantments);

                            return meta;
                        }

                        return meta.enchantments(enchantments);
                    });
                })
                .collect(Collectors.toList());
    }
}
