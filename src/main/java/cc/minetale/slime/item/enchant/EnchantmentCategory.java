package cc.minetale.slime.item.enchant;

import lombok.Getter;
import net.minestom.server.item.Enchantment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum EnchantmentCategory implements IEnchantmentMarker<Enchantment> {
    ARMOR,
    ARMOR_HEAD,
    ARMOR_CHEST,
    ARMOR_FEET,
    WEARABLE,
    BREAKABLE,
    DIGGER,
    WEAPON,
    BOW,
    CROSSBOW,
    TRIDENT,
    FISHING_ROD,
    VANISHABLE;

    static {
        for(var category : EnchantmentCategory.values()) {
            category.enchantments = Enchantment.values().stream()
                    .filter(enchantment -> {
                        var registry = enchantment.registry();
                        var categoryId = registry.category();

                        var enchantmentCategory = EnchantmentCategory.valueOf(categoryId);
                        return enchantmentCategory == category;
                    })
                    .collect(Collectors.toCollection(() -> Collections.synchronizedSet(new HashSet<>())));
        }
    }

    private Set<Enchantment> enchantments;

    @Override
    public Set<Enchantment> getMarked() {
        return this.enchantments;
    }

    @Override
    public boolean isMarked(Enchantment value) {
        return this.enchantments.contains(value);
    }


}
