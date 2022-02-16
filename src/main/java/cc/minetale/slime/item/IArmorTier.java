package cc.minetale.slime.item;

import net.kyori.adventure.key.Key;

import java.util.List;

public interface IArmorTier extends ITier {
    int getDurabilityMultiplier();
    List<Integer> getProtectionAmounts();

    Key getEquipSound();

    float getToughness();
    float getKnockbackResistance();
}
