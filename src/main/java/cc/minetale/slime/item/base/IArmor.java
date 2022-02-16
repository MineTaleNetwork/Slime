package cc.minetale.slime.item.base;

import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.inventory.EquipmentHandler;

public interface IArmor extends IItem {
    int getDurabilityMultiplier(Entity victim, DamageType type, float damage);

    Key getEquipSoundFor(EquipmentHandler wearer);

    float getToughness(EquipmentHandler wearer);
    float getKnockbackResistance(EquipmentHandler wearer);
}
