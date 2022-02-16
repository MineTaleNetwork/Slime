package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cc.minetale.slime.Slime.TAG_MANAGER;

@Getter
public class PredicateItem {
    @Setter private IntegerRangeProvider count;
    @Setter private IntegerRangeProvider durability;
    private final List<PredicateEnchantment> enchantments;
    private final List<PredicateEnchantment> storedEnchantments;
    private final List<NamespaceID> items;
    @Setter private String tag;

    public PredicateItem(@Nullable IntegerRangeProvider count,
                         @Nullable IntegerRangeProvider durability,
                         @Nullable List<PredicateEnchantment> enchantments,
                         @Nullable List<PredicateEnchantment> storedEnchantments,
                         @Nullable List<NamespaceID> items,
                         @Nullable String tag) {

        this.count = count;
        this.durability = durability;
        this.tag = tag;

        this.enchantments = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(enchantments, Collections.emptyList())));

        this.storedEnchantments = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(storedEnchantments, Collections.emptyList())));

        this.items = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(items, Collections.emptyList())));
    }

    public boolean test(ItemStack item) {
        if(this.count != null && !this.count.isInRange(item.getAmount())) { return false; }

        if(this.durability != null) {
            var meta = item.getMeta();
            var damage = meta.getDamage();
            if(!this.durability.isInRange(damage)) { return false; }
        }

        if(!this.enchantments.isEmpty() && this.enchantments
                .stream()
                .noneMatch(enchantment -> enchantment.test(item))) {

            return false;
        }

        //Not used in files?
        if(!this.storedEnchantments.isEmpty() && this.storedEnchantments
                .stream()
                .noneMatch(enchantment -> enchantment.test(item))) {

            return false;
        }

        var id = item.getMaterial().namespace();
        if(!this.items.isEmpty() && !this.items.contains(id))
            return false;

        if(this.tag != null) {
            var tag = TAG_MANAGER.getTag(Tag.BasicType.ITEMS, this.tag);
            return tag == null || !tag.contains(id);
        }

        return true;
    }
}
