package cc.minetale.slime.loot.function;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.item.base.IItem;
import cc.minetale.slime.item.enchant.IEnchantable;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static cc.minetale.slime.Slime.ITEM_MANAGER;

@Getter @Setter
public class EnchantRandomlyFunction extends LootFunction {
    private final List<NamespaceID> enchantments;

    @JsonCreator
    protected EnchantRandomlyFunction(List<NamespaceID> enchantments, List<LootPredicate> conditions) {
        super(FunctionType.ENCHANT_RANDOMLY, conditions);

        this.enchantments = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(enchantments, Collections.emptyList())));
    }

    @Override
    public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        return loot
                .stream()
                .map(itemStack -> {
                    return itemStack.withMeta(meta -> {
                        var rng = ThreadLocalRandom.current();
                        Enchantment enchantment;

                        if(!this.enchantments.isEmpty()) {
                            var enchantmentId = this.enchantments.stream()
                                    .skip((long) (this.enchantments.size() * rng.nextDouble()))
                                    .findFirst()
                                    .orElse(null);

                            if(enchantmentId == null) { return meta; }

                            enchantment = Enchantment.fromNamespaceId(enchantmentId);
                        } else {
                            IEnchantable item = null;

                            var isTagged = ITEM_MANAGER.isTagged(itemStack);
                            if(isTagged) {
                                var possibleItem = ITEM_MANAGER.getItemFromTag(itemStack);
                                if(!(possibleItem instanceof IEnchantable enchantable)) { return meta; }

                                item = enchantable;
                            } else {
                                Set<IItem> possibleItems = ITEM_MANAGER.getItemsByMaterial(itemStack.getMaterial());
                                if(possibleItems == null || possibleItems.isEmpty()) { return meta; }

                                for(var possibleItem : possibleItems) {
                                    if(possibleItem instanceof IEnchantable enchantable) {
                                        item = enchantable;
                                        break;
                                    }
                                }
                            }

                            if(item == null) { return meta; }

                            Set<Enchantment> enchantments = item.getAllEnchantments();
                            if(enchantments == null || enchantments.isEmpty()) { return meta; }

                            enchantment = CollectionsUtil.random(enchantments);
                        }

                        if(enchantment == null) { return meta; }

                        short level = (short) Math.round(((enchantment.registry().maxLevel() - 1d) * rng.nextDouble()) + 1d);

                        return meta.enchantment(enchantment, level);
                    });
                })
                .collect(Collectors.toList());
    }
}
