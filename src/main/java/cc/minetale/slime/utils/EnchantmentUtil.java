package cc.minetale.slime.utils;

import cc.minetale.commonlib.util.JsonUtil;
import lombok.experimental.UtilityClass;
import net.minestom.server.item.Enchantment;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class EnchantmentUtil {

    private static final int[] DAMAGE_BASE_POWERS = new int[]{1, 5, 5};
    private static final int[] DAMAGE_POWERS_PER_LEVEL = new int[]{11, 8, 8};

//    It's the same for all types so just replace it with just a number
//    private static final int[] DAMAGE_MIN_MAX_POWER_DIFFERENCES = new int[]{20, 20, 20};

    private static final int[] PROTECTION_BASE_POWERS = new int[]{1, 10, 5, 5, 3};
    private static final int[] PROTECTION_POWERS_PER_LEVEL = new int[]{11, 8, 6, 8, 6};

    public static void removeConflicts(Map<Enchantment, Short> enchantments, Map.Entry<Enchantment, Short> compareTo) {
        var compareToEnchant = compareTo.getKey();
        for(final var it = enchantments.entrySet().iterator(); it.hasNext();) {
            if (!getIncompatibleEnchantments(compareToEnchant).contains(it.next().getKey())) { continue; }
            it.remove();
        }
    }

    public static List<Enchantment> getIncompatibleEnchantments(Enchantment enchantment) {
        var registry = enchantment.registry();

        var incompatData = registry.getString("incompatibleEnchantments");

        String[] incompatIds = JsonUtil.readFromJson(incompatData, String[].class);
        if(incompatIds == null || incompatIds.length == 0) { return Collections.emptyList(); }

        List<Enchantment> incompatible = new LinkedList<>();

        for(var incompatId : incompatIds) {
            var incompatEnchant = Enchantment.fromNamespaceId(incompatId);
            if(incompatEnchant == null) { continue; }

            incompatible.add(incompatEnchant);
        }

        return incompatible;
    }

    public static int getMinPower(Enchantment enchantment, int level) {
        if(isDamageEnchantment(enchantment)) {
            return getDamageEnchantmentMinPowerCost(enchantment, level);
        } else if(isProtectionEnchantment(enchantment)) {
            return getProtectionEnchantmentMinPowerCost(enchantment, level);
        } else if(Enchantment.LURE.equals(enchantment) ||
                isLuckEnchantment(enchantment)) {

            return 15 + (level - 1) * 9;
        } else if(Enchantment.DEPTH_STRIDER.equals(enchantment) ||
                Enchantment.FROST_WALKER.equals(enchantment) ||
                Enchantment.SOUL_SPEED.equals(enchantment) ||
                Enchantment.RESPIRATION.equals(enchantment)) {

            return level * 10;
        } else if(Enchantment.BINDING_CURSE.equals(enchantment) ||
                Enchantment.CHANNELING.equals(enchantment) ||
                Enchantment.VANISHING_CURSE.equals(enchantment)) {

            return 25;
        } else if(Enchantment.FLAME.equals(enchantment) ||
                Enchantment.INFINITY.equals(enchantment) ||
                Enchantment.MULTISHOT.equals(enchantment)) {

            return 20;
        } else if(Enchantment.FIRE_ASPECT.equals(enchantment) ||
                Enchantment.THORNS.equals(enchantment)) {

            return 10 + 20 * (level - 1);
        } else if(Enchantment.PUNCH.equals(enchantment) ||
                Enchantment.QUICK_CHARGE.equals(enchantment)) {

            return 12 + (level - 1) * 20;
        } else if(Enchantment.SWEEPING.equals(enchantment) ||
                Enchantment.UNBREAKING.equals(enchantment)) {

            return 5 + (level - 1) * 9;
        } else if(Enchantment.PIERCING.equals(enchantment) ||
                Enchantment.POWER.equals(enchantment)) {

            return 1 + (level - 1) * 10;
        } else if(Enchantment.EFFICIENCY.equals(enchantment)) {
            return 1 + 10 * (level - 1);
        } else if(Enchantment.IMPALING.equals(enchantment)) {
            return 1 + (level - 1) * 8;
        } else if(Enchantment.KNOCKBACK.equals(enchantment)) {
            return 5 + 20 * (level - 1);
        } else if(Enchantment.LOYALTY.equals(enchantment)) {
            return 5 + level * 7;
        } else if(Enchantment.MENDING.equals(enchantment)) {
            return level * 25;
        } else if(Enchantment.RIPTIDE.equals(enchantment)) {
            return 10 + level * 7;
        } else if(Enchantment.SILK_TOUCH.equals(enchantment)) {
            return 15;
        } else if(Enchantment.AQUA_AFFINITY.equals(enchantment)) {
            return 1;
        }

        return 1;
    }

    public static int getMaxPower(Enchantment enchantment, int level) {
        if(isDamageEnchantment(enchantment)) {
            return getDamageEnchantmentMaxPowerCost(enchantment, level);
        } else if(isProtectionEnchantment(enchantment)) {
            return getProtectionEnchantmentMaxPowerCost(enchantment, level);
        } else if(Enchantment.BINDING_CURSE.equals(enchantment) ||
                Enchantment.CHANNELING.equals(enchantment) ||
                Enchantment.FLAME.equals(enchantment) ||
                Enchantment.INFINITY.equals(enchantment) ||
                Enchantment.MULTISHOT.equals(enchantment) ||
                Enchantment.QUICK_CHARGE.equals(enchantment) ||
                Enchantment.PIERCING.equals(enchantment) ||
                Enchantment.LOYALTY.equals(enchantment) ||
                Enchantment.RIPTIDE.equals(enchantment) ||
                Enchantment.VANISHING_CURSE.equals(enchantment)) {

            return 50;
        } else if(Enchantment.FIRE_ASPECT.equals(enchantment) ||
                Enchantment.THORNS.equals(enchantment) ||
                Enchantment.UNBREAKING.equals(enchantment) ||
                Enchantment.EFFICIENCY.equals(enchantment) ||
                Enchantment.KNOCKBACK.equals(enchantment) ||
                Enchantment.MENDING.equals(enchantment) ||
                Enchantment.SILK_TOUCH.equals(enchantment) ||
                Enchantment.LURE.equals(enchantment) ||
                isLuckEnchantment(enchantment)) {

            return getMinPower(enchantment, level) + 50;
        } else if(Enchantment.DEPTH_STRIDER.equals(enchantment) ||
                Enchantment.FROST_WALKER.equals(enchantment) ||
                Enchantment.SOUL_SPEED.equals(enchantment) ||
                Enchantment.SWEEPING.equals(enchantment) ||
                Enchantment.POWER.equals(enchantment)) {

            return getMinPower(enchantment, level) + 15;
        } else if(Enchantment.IMPALING.equals(enchantment)) {
            return getMinPower(enchantment, level) + 20;
        } else if(Enchantment.PUNCH.equals(enchantment)) {
            return getMinPower(enchantment, level) + 25;
        } else if(Enchantment.RESPIRATION.equals(enchantment)) {
            return getMinPower(enchantment, level) + 30;
        } else if(Enchantment.AQUA_AFFINITY.equals(enchantment)) {
            return getMinPower(enchantment, level) + 40;
        }

        return 1;
    }

    public static boolean isDamageEnchantment(Enchantment enchantment) {
        return Enchantment.SHARPNESS.equals(enchantment) ||
                Enchantment.SMITE.equals(enchantment) ||
                Enchantment.BANE_OF_ARTHROPODS.equals(enchantment);
    }

    public static int getDamageEnchantmentIndex(Enchantment enchantment) {
        if(Enchantment.SHARPNESS.equals(enchantment)){
            return 0;
        } else if(Enchantment.SMITE.equals(enchantment)) {
            return 1;
        } else if(Enchantment.BANE_OF_ARTHROPODS.equals(enchantment)) {
            return 2;
        }

        return -1;
    }

    public static int getDamageEnchantmentMinPowerCost(Enchantment enchantment, int level) {
        final int type = getDamageEnchantmentIndex(enchantment);
        if(type == -1) { return 1; }

        return DAMAGE_BASE_POWERS[type] + (level - 1) * DAMAGE_POWERS_PER_LEVEL[type];
    }

    public static int getDamageEnchantmentMaxPowerCost(Enchantment enchantment, int level) {
        final int type = getDamageEnchantmentIndex(enchantment);
        if(type == -1) { return 1; }

        //MIN_MAX_POWER_DIFFERENCES is the same for all types? Use constant 20 unless that changes
        return getProtectionEnchantmentMinPowerCost(enchantment, level) + 20;
    }

    public static boolean isLuckEnchantment(Enchantment enchantment) {
        return Enchantment.LOOTING.equals(enchantment) ||
                Enchantment.FORTUNE.equals(enchantment) ||
                Enchantment.LUCK_OF_THE_SEA.equals(enchantment);
    }

    public static boolean isProtectionEnchantment(Enchantment enchantment) {
        return Enchantment.PROTECTION.equals(enchantment) ||
                Enchantment.FIRE_PROTECTION.equals(enchantment) ||
                Enchantment.FEATHER_FALLING.equals(enchantment) ||
                Enchantment.BLAST_PROTECTION.equals(enchantment) ||
                Enchantment.PROJECTILE_PROTECTION.equals(enchantment);
    }

    public static int getProtectionEnchantmentIndex(Enchantment enchantment) {
        if(Enchantment.PROTECTION.equals(enchantment)) {
            return 0;
        } else if(Enchantment.FIRE_PROTECTION.equals(enchantment)) {
            return 1;
        } else if(Enchantment.FEATHER_FALLING.equals(enchantment)) {
            return 2;
        } else if(Enchantment.BLAST_PROTECTION.equals(enchantment)) {
            return 3;
        } else if(Enchantment.PROJECTILE_PROTECTION.equals(enchantment)) {
            return 4;
        }

        return -1;
    }

    public static int getProtectionEnchantmentMinPowerCost(Enchantment enchantment, int level) {
        final int type = getProtectionEnchantmentIndex(enchantment);
        if(type == -1) { return 1; }

        return PROTECTION_BASE_POWERS[type] + (level - 1) * PROTECTION_POWERS_PER_LEVEL[type];
    }


    public static int getProtectionEnchantmentMaxPowerCost(Enchantment enchantment, int level) {
        final int type = getProtectionEnchantmentIndex(enchantment);
        if(type == -1) { return 1; }

        return getProtectionEnchantmentMinPowerCost(enchantment, level) + PROTECTION_POWERS_PER_LEVEL[type];
    }

}
