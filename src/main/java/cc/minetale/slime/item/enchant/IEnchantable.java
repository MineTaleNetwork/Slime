package cc.minetale.slime.item.enchant;

import net.minestom.server.item.Enchantment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Enchantments applicable to an item are split into two groups. <br>
 * <ul>
 *     <li>Primary enchantments can be found while enchanting an item in an enchanting table.</li>
 *     <li>Secondary enchantments cannot be found in an enchanting table, <br>
 *     but can be retrieved through any other means like loot functions, enchanted books, etc.</li>
 * </ul>
 * No matter what group an enchantment is marked in, it can be (safely; as expected) applied to an item.
 */
public interface IEnchantable {
    int getEnchantability();

    IEnchantmentMarker<?> getPrimaryEnchantmentMarker();
    IEnchantmentMarker<?> getSecondaryEnchantmentMarker();

    default Set<Enchantment> getPrimaryEnchantments() {
        IEnchantmentMarker<?> marker = getPrimaryEnchantmentMarker();
        if(marker == null) { return Collections.emptySet(); }

        return new HashSet<>(marker.getMarked());
    }

    default Set<Enchantment> getSecondaryEnchantments() {
        IEnchantmentMarker<?> marker = getSecondaryEnchantmentMarker();
        if(marker == null) { return Collections.emptySet(); }

        return new HashSet<>(marker.getMarked());
    }

    default Set<Enchantment> getAllEnchantments() {
        Set<Enchantment> enchantments = new HashSet<>();
        enchantments.addAll(getPrimaryEnchantments());
        enchantments.addAll(getSecondaryEnchantments());
        return enchantments;
    }
}
